import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { bootstrapApplication } from '@angular/platform-browser';
import { App } from './app/app';
import { appConfig } from './app/app.config';

// Nombres et dates au format francais (1 234,56)
registerLocaleData(localeFr);

bootstrapApplication(App, appConfig).catch((err) => console.error(err));
