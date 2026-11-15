package ma.emsi.station.statistiques.services;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.emsi.station.statistiques.dtos.*;
import ma.emsi.station.statistiques.exceptions.BusinessException;
import ma.emsi.station.statistiques.feign.AchatClient;
import ma.emsi.station.statistiques.feign.ClientClient;
import ma.emsi.station.statistiques.feign.StockClient;
import ma.emsi.station.statistiques.feign.VenteClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Service de pilotage : pas de base de donnees propre, il interroge les autres
 * microservices via OpenFeign et calcule les indicateurs, en particulier les pertes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatistiquesService {

    private static final BigDecimal TVA_DEFAUT = new BigDecimal("0.10");

    private final VenteClient venteClient;
    private final StockClient stockClient;
    private final AchatClient achatClient;
    private final ClientClient clientClient;

    // ------------------------------------------------------------------ tableau de bord
    public DashboardDTO dashboard(LocalDate date) {
        List<String> indisponibles = new ArrayList<>();

        List<VenteClient.ResumeCarburant> ventes = safe(() -> venteClient.resume(date, date), List.of(), "vente-service", indisponibles);
        List<VenteClient.Cloture> clotures = safe(() -> venteClient.clotures(date, date), List.of(), "vente-service", indisponibles);
        List<StockClient.Cuve> cuves = safe(stockClient::cuves, List.of(), "stock-service", indisponibles);
        List<StockClient.Jaugeage> jaugeages = safe(() -> stockClient.jaugeages(date, date), List.of(), "stock-service", indisponibles);
        ClientClient.Encours encours = safe(clientClient::encours, null, "client-service", indisponibles);

        BigDecimal ca = ventes.stream().map(VenteClient.ResumeCarburant::chiffreAffaires).reduce(BigDecimal.ZERO, BigDecimal::add);
        double volume = ventes.stream().mapToDouble(VenteClient.ResumeCarburant::volumeTotal).sum();
        long nb = ventes.stream().mapToLong(VenteClient.ResumeCarburant::nombreVentes).sum();
        List<StockClient.Cuve> enAlerte = cuves.stream().filter(StockClient.Cuve::enAlerte).toList();

        List<String> alertes = new ArrayList<>();
        enAlerte.forEach(c -> alertes.add(String.format("Stock bas : cuve %s (%s) a %.0f L, seuil %.0f L",
                c.code(), c.carburantNom(), c.volumeActuel(), c.seuilAlerte())));
        jaugeages.stream().filter(StockClient.Jaugeage::anomalie).forEach(j -> alertes.add(String.format(
                "Coulage : cuve %s, ecart de %.1f L (%.2f %%, tolerance %.2f %%)",
                j.cuveCode(), j.ecart(), j.ecartPourcentage(), j.toleranceAppliquee())));
        clotures.stream().filter(VenteClient.Cloture::ecartHorsTolerance).forEach(c -> alertes.add(String.format(
                "Ecart de caisse : %s, %s DH (tolere : %s DH)", c.pompiste(), c.ecart(), c.seuilEcartApplique())));
        clotures.stream().filter(c -> c.nbIndexDiscontinus() > 0).forEach(c -> alertes.add(String.format(
                "Index discontinu : %s, %d pistolet(s) a verifier", c.pompiste(), c.nbIndexDiscontinus())));

        return new DashboardDTO(date, ca, volume, nb, ventes, cuves, enAlerte, encours, alertes, indisponibles);
    }

    public List<VenteClient.CaJournalier> evolutionCa(LocalDate debut, LocalDate fin) {
        try {
            return venteClient.caJournalier(debut, fin);
        } catch (FeignException e) {
            throw BusinessException.unavailable("vente-service indisponible");
        }
    }

    // ------------------------------------------------------------------ pertes
    /**
     * Pertes de la station sur la periode :
     * - coulage : somme des ecarts negatifs des jaugeages, valorises au prix d'achat moyen
     * - caisse : somme des ecarts negatifs des clotures (argent manquant)
     */
    public RapportPertesDTO pertes(LocalDate debut, LocalDate fin) {
        List<StockClient.Jaugeage> jaugeages;
        List<VenteClient.Cloture> clotures;
        try {
            jaugeages = stockClient.jaugeages(debut, fin);
            clotures = venteClient.clotures(debut, fin);
        } catch (FeignException e) {
            throw BusinessException.unavailable("Donnees de stock ou de caisse indisponibles");
        }
        Map<Long, BigDecimal> prixAchat = prixAchatParCarburant();

        List<PerteCuveDTO> parCuve = jaugeages.stream()
                .collect(Collectors.groupingBy(StockClient.Jaugeage::cuveCode, TreeMap::new, Collectors.toList()))
                .values().stream()
                .map(liste -> {
                    StockClient.Jaugeage premier = liste.get(0);
                    double ecart = liste.stream().mapToDouble(StockClient.Jaugeage::ecart).sum();
                    BigDecimal prix = prixAchat.getOrDefault(premier.carburantId(), BigDecimal.ZERO);
                    BigDecimal valeur = prix.multiply(BigDecimal.valueOf(ecart)).setScale(2, RoundingMode.HALF_UP);
                    double pire = liste.stream().mapToDouble(StockClient.Jaugeage::ecartPourcentage)
                            .min().orElse(0);
                    int anomalies = (int) liste.stream().filter(StockClient.Jaugeage::anomalie).count();
                    return new PerteCuveDTO(premier.cuveCode(), premier.carburantNom(), liste.size(), anomalies,
                            arrondi(ecart), valeur, pire, premier.toleranceAppliquee());
                })
                .toList();

        double perteLitres = jaugeages.stream().mapToDouble(StockClient.Jaugeage::ecart).filter(e -> e < 0).sum();
        BigDecimal perteDH = jaugeages.stream()
                .filter(j -> j.ecart() < 0)
                .map(j -> prixAchat.getOrDefault(j.carburantId(), BigDecimal.ZERO).multiply(BigDecimal.valueOf(-j.ecart())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        BigDecimal manquants = clotures.stream().map(VenteClient.Cloture::ecart).filter(e -> e != null && e.signum() < 0)
                .map(BigDecimal::negate).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RapportPertesDTO(debut, fin, parCuve, arrondi(-perteLitres), perteDH, manquants,
                perteDH.add(manquants),
                (int) jaugeages.stream().filter(StockClient.Jaugeage::anomalie).count(),
                (int) clotures.stream().filter(VenteClient.Cloture::ecartHorsTolerance).count(),
                clotures.stream().mapToInt(VenteClient.Cloture::nbIndexDiscontinus).sum());
    }

    /** Bilan par pompiste, trie du plus gros manquant au plus petit. */
    public List<PerformancePompisteDTO> pompistes(LocalDate debut, LocalDate fin) {
        List<VenteClient.Cloture> clotures;
        try {
            clotures = venteClient.clotures(debut, fin);
        } catch (FeignException e) {
            throw BusinessException.unavailable("vente-service indisponible");
        }
        return clotures.stream()
                .collect(Collectors.groupingBy(VenteClient.Cloture::pompiste))
                .entrySet().stream()
                .map(e -> {
                    List<VenteClient.Cloture> liste = e.getValue();
                    BigDecimal ecartTotal = liste.stream().map(VenteClient.Cloture::ecart).filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal manquants = liste.stream().map(VenteClient.Cloture::ecart)
                            .filter(x -> x != null && x.signum() < 0).map(BigDecimal::negate)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal ca = liste.stream().map(VenteClient.Cloture::montantCompteurs).filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    double volume = liste.stream().map(VenteClient.Cloture::volumeCompteurs).filter(Objects::nonNull)
                            .mapToDouble(Double::doubleValue).sum();
                    return new PerformancePompisteDTO(e.getKey(), liste.size(), arrondi(volume), ca, ecartTotal,
                            manquants, ecartTotal.divide(BigDecimal.valueOf(liste.size()), 2, RoundingMode.HALF_UP),
                            (int) liste.stream().filter(VenteClient.Cloture::ecartHorsTolerance).count(),
                            liste.stream().mapToInt(VenteClient.Cloture::nbIndexDiscontinus).sum());
                })
                .sorted(Comparator.comparing(PerformancePompisteDTO::manquantsDH).reversed())
                .toList();
    }

    // ------------------------------------------------------------------ marges
    /** Marge brute HT par carburant = CA HT - (volume vendu x prix d'achat moyen HT). */
    public RapportMargesDTO marges(LocalDate debut, LocalDate fin) {
        List<VenteClient.ResumeCarburant> ventes;
        Map<Long, AchatClient.PrixAchat> prix;
        try {
            ventes = venteClient.resume(debut, fin);
            prix = achatClient.prixAchatMoyens().stream()
                    .collect(Collectors.toMap(AchatClient.PrixAchat::carburantId, Function.identity()));
        } catch (FeignException e) {
            throw BusinessException.unavailable("Donnees de ventes ou d'achat indisponibles");
        }
        // Le taux de TVA vient du parametrage financier de client-service (10 % par defaut si indisponible)
        BigDecimal tva = safe(() -> clientClient.parametres().tauxTva(), TVA_DEFAUT, "client-service", new ArrayList<>());

        BigDecimal diviseur = BigDecimal.ONE.add(tva);
        List<MargeCarburantDTO> lignes = ventes.stream().map(v -> {
            BigDecimal caHT = v.chiffreAffaires().divide(diviseur, 2, RoundingMode.HALF_UP);
            BigDecimal pa = prix.containsKey(v.carburantId()) ? prix.get(v.carburantId()).prixAchatMoyen() : BigDecimal.ZERO;
            BigDecimal cout = pa.multiply(BigDecimal.valueOf(v.volumeTotal())).setScale(2, RoundingMode.HALF_UP);
            BigDecimal marge = caHT.subtract(cout);
            return new MargeCarburantDTO(v.carburantNom(), v.volumeTotal(), v.chiffreAffaires(), caHT, pa, cout,
                    marge, pourcentage(marge, caHT));
        }).toList();

        BigDecimal caHT = lignes.stream().map(MargeCarburantDTO::chiffreAffairesHT).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal marge = lignes.stream().map(MargeCarburantDTO::margeBruteHT).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new RapportMargesDTO(debut, fin, lignes, caHT, marge, pourcentage(marge, caHT));
    }

    // ------------------------------------------------------------------ utilitaires
    private Map<Long, BigDecimal> prixAchatParCarburant() {
        return safe(() -> achatClient.prixAchatMoyens().stream()
                        .collect(Collectors.toMap(AchatClient.PrixAchat::carburantId,
                                AchatClient.PrixAchat::prixAchatMoyen)),
                Map.<Long, BigDecimal>of(), "achat-service", new ArrayList<>());
    }

    private static double arrondi(double litres) {
        return Math.round(litres * 1000) / 1000.0;
    }

    private static BigDecimal pourcentage(BigDecimal part, BigDecimal total) {
        return total.signum() == 0 ? BigDecimal.ZERO
                : part.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP);
    }

    /** Isolation des pannes : si un service ne repond pas, on renvoie une valeur par defaut. */
    private <T> T safe(Supplier<T> call, T fallback, String service, List<String> indisponibles) {
        try {
            T result = call.get();
            return result != null ? result : fallback;
        } catch (Exception e) {
            log.warn("{} indisponible : {}", service, e.getMessage());
            if (!indisponibles.contains(service)) {
                indisponibles.add(service);
            }
            return fallback;
        }
    }
}
