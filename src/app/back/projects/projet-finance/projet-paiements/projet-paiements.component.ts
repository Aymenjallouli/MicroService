import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Paiement } from '../../../../models/paiement.model';
import { PaiementService } from '../../../../Services/paiement.service';
import { FactureService } from '../../../../Services/facture.service';
import { Facture } from '../../../../models/facture.model';

@Component({
  selector: 'app-projet-paiements',
  templateUrl: './projet-paiements.component.html',
  styleUrls: ['./projet-paiements.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class ProjetPaiementsComponent implements OnInit {
  @Input() projectId: number = 0;
  paiements: Paiement[] = [];
  factures: Facture[] = [];
  isLoading: boolean = false;

  constructor(
    private paiementService: PaiementService,
    private factureService: FactureService,
    private toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.loadFactures();
    this.loadPaiements();
  }  loadFactures() {
    // Initialiser avec un tableau vide
    this.factures = [];
    
    this.factureService.getFacturesByProjectId(this.projectId).subscribe({
      next: (data) => {
        if (data) {
          this.factures = data;
        }
      },
      error: (error) => {
        console.error('Error loading factures for payments', error);
        this.toastr.warning('Impossible de charger les factures - le filtrage des paiements peut être incomplet');
      }
    });
  }

  loadPaiements() {
    this.isLoading = true;
    // Initialiser avec un tableau vide
    this.paiements = [];
    
    this.paiementService.getAllPaiements().subscribe({
      next: (data) => {
        if (data && data.length > 0 && this.factures.length > 0) {
          // Filtrer les paiements par factures du projet
          const factureIds = this.factures.map(f => f.id);
          this.paiements = data.filter(p => p.facture && factureIds.includes(p.facture.id));
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading paiements', error);
        this.isLoading = false;
        this.toastr.warning('Impossible de charger les paiements - vérifiez la connexion au serveur');
      }
    });
  }

  deletePaiement(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce paiement ?')) {
      this.isLoading = true;
      this.paiementService.deletePaiement(id).subscribe({
        next: () => {
          this.toastr.success('Paiement supprimé avec succès');
          this.loadPaiements();
        },
        error: (error) => {
          console.error('Error deleting paiement', error);
          this.isLoading = false;
          this.toastr.error('Erreur lors de la suppression du paiement');
        }
      });
    }
  }
}
