/** Interfaces TypeScript = DTO renvoyes par les microservices (memes noms de champs). */

export type Role = 'ADMIN' | 'GERANT' | 'POMPISTE' | 'COMPTABLE';
export type ModePaiement = 'ESPECES' | 'CARTE' | 'CHEQUE' | 'CREDIT';

// ---------------- authentification-service
export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  username: string;
  nomComplet: string | null;
  role: Role;
}

export interface Utilisateur {
  id: number;
  username: string;
  nomComplet: string | null;
  role: Role;
  actif: boolean;
}

// ---------------- stock-service
export interface Carburant {
  id: number;
  nom: string;
  densite: number | null;
  prixVente: number;
  toleranceCoulagePct: number;
}

export interface Cuve {
  id: number;
  code: string;
  capaciteMax: number;
  volumeActuel: number;
  seuilAlerte: number;
  carburantId: number;
  carburantNom: string;
  prixVente: number;
  tauxRemplissage: number;
  enAlerte: boolean;
}

export interface Jaugeage {
  id: number;
  dateReleve: string;
  cuveId: number;
  cuveCode: string;
  carburantId: number;
  carburantNom: string;
  volumeTheorique: number;
  volumeMesure: number;
  ecart: number;
  ecartPourcentage: number;
  toleranceAppliquee: number;
  anomalie: boolean;
}

// ---------------- achat-service
export interface Fournisseur {
  id: number | null;
  nom: string;
  telephone: string | null;
  email: string | null;
  adresse: string | null;
}

export interface Livraison {
  id: number;
  dateLivraison: string;
  numeroBon: string | null;
  quantite: number;
  prixAchatUnitaire: number;
  montantTotal: number;
  cuveId: number;
  cuveCode: string;
  carburantNom: string;
  fournisseurId: number;
  fournisseurNom: string;
}

// ---------------- vente-service
export interface Pistolet {
  id: number;
  numero: number;
  cuveId: number;
  pompeNumero: number;
  dernierIndex: number;
  active: boolean;
}

export interface Poste {
  id: number;
  pompiste: string;
  dateOuverture: string;
  dateCloture: string | null;
  fondCaisse: number;
  statut: 'OUVERT' | 'CLOTURE';
}

export interface ReleveIndex {
  id: number;
  pistoletId: number;
  pompeNumero: number;
  pistoletNumero: number;
  cuveId: number;
  dernierIndexConnu: number;
  indexDebut: number;
  indexFin: number | null;
  volumeCompteur: number | null;
  prixUnitaire: number | null;
  montantCompteur: number | null;
  indexDiscontinu: boolean;
}

export interface Vente {
  id: number;
  dateVente: string;
  pompeNumero: number;
  pistoletNumero: number;
  carburantNom: string;
  volume: number;
  prixUnitaire: number;
  montantTotal: number;
  modePaiement: ModePaiement;
  clientId: number | null;
  posteId: number;
  pompiste: string;
}

export interface Cloture {
  id: number;
  posteId: number;
  pompiste: string;
  dateCloture: string;
  fondCaisse: number;
  volumeCompteurs: number;
  montantCompteurs: number;
  totalEspeces: number;
  totalCarte: number;
  totalCheque: number;
  totalCredit: number;
  especesAttendues: number;
  totalTheorique: number;
  totalPhysique: number;
  ecart: number;
  seuilEcartApplique: number;
  ecartHorsTolerance: boolean;
  nbIndexDiscontinus: number;
  releves: ReleveIndex[];
}

export interface ParametreControle {
  seuilEcartCaisse: number;
  controleContinuiteIndex: boolean;
}

// ---------------- client-service
export interface Client {
  id: number;
  raisonSociale: string;
  ice: string | null;
  telephone: string | null;
  email: string | null;
  adresse: string | null;
  plafondCredit: number;
  soldeActuel: number;
  disponible: number;
  actif: boolean;
}

export interface LigneFacture {
  designation: string;
  quantite: number;
  prixUnitaireHT: number;
  montantHT: number;
  montantTTC: number;
}

export interface Facture {
  id: number;
  numeroFacture: string;
  dateFacture: string;
  periodeDebut: string;
  periodeFin: string;
  clientId: number;
  raisonSociale: string;
  ice: string | null;
  tauxTva: number;
  montantHT: number;
  montantTVA: number;
  montantTTC: number;
  montantRegle: number;
  resteAPayer: number;
  statut: 'EMISE' | 'PARTIELLEMENT_PAYEE' | 'PAYEE';
  lignes: LigneFacture[];
}

export interface Reglement {
  id: number;
  dateReglement: string;
  montant: number;
  modeReglement: string;
  reference: string | null;
  factureId: number;
  numeroFacture: string;
  statutFacture: string;
}

export interface ParametreFinancier {
  tauxTva: number;
  seuilAlerteEncours: number;
}

// ---------------- statistiques-service
export interface ResumeCarburant {
  carburantId: number;
  carburantNom: string;
  volumeTotal: number;
  chiffreAffaires: number;
  nombreVentes: number;
}

export interface Encours {
  encoursTotal: number;
  impayesFactures: number;
  nombreClients: number;
  clientsProchesPlafond: { id: number; raisonSociale: string; plafondCredit: number; soldeActuel: number }[];
}

export interface Dashboard {
  date: string;
  chiffreAffaires: number;
  volumeTotal: number;
  nombreVentes: number;
  ventesParCarburant: ResumeCarburant[];
  cuves: Cuve[];
  cuvesEnAlerte: Cuve[];
  encoursClients: Encours | null;
  alertes: string[];
  servicesIndisponibles: string[];
}

export interface PerteCuve {
  cuveCode: string;
  carburantNom: string;
  nombreJaugeages: number;
  nombreAnomalies: number;
  ecartLitres: number;
  ecartValeurDH: number;
  pireEcartPct: number;
  tolerancePct: number;
}

export interface RapportPertes {
  debut: string;
  fin: string;
  parCuve: PerteCuve[];
  perteCarburantLitres: number;
  perteCarburantDH: number;
  manquantsCaisseDH: number;
  totalPertesDH: number;
  nombreAnomaliesJaugeage: number;
  nombreEcartsCaisseHorsTolerance: number;
  nombreIndexDiscontinus: number;
}

export interface PerformancePompiste {
  pompiste: string;
  nombrePostes: number;
  volumeVendu: number;
  chiffreAffaires: number;
  ecartTotal: number;
  manquantsDH: number;
  ecartMoyen: number;
  nombreEcartsHorsTolerance: number;
  nombreIndexDiscontinus: number;
}

export interface MargeCarburant {
  carburant: string;
  volumeVendu: number;
  chiffreAffairesTTC: number;
  chiffreAffairesHT: number;
  prixAchatMoyenHT: number;
  coutAchatHT: number;
  margeBruteHT: number;
  tauxMargePct: number;
}

export interface RapportMarges {
  debut: string;
  fin: string;
  parCarburant: MargeCarburant[];
  chiffreAffairesHT: number;
  margeBruteHT: number;
  tauxMargeGlobalPct: number;
}

export interface CaJournalier {
  date: string;
  chiffreAffaires: number;
  volume: number;
  nombreVentes: number;
}
