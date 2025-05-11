import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { ProjetFinanceComponent } from './projet-finance.component';
import { ProjetFacturesComponent } from './projet-factures/projet-factures.component';
import { ProjetPaiementsComponent } from './projet-paiements/projet-paiements.component';

@NgModule({
  declarations: [
    ProjetFinanceComponent,
    ProjetFacturesComponent,
    ProjetPaiementsComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule
  ],
  exports: [
    ProjetFinanceComponent,
    ProjetFacturesComponent,
    ProjetPaiementsComponent
  ]
})
export class FinanceModule { }
