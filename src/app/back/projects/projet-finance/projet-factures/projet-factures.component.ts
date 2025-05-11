import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { FactureService } from '../../../../Services/facture.service';
import { Facture } from '../../../../models/facture.model';
import { PaiementService } from '../../../../Services/paiement.service';
import { Paiement } from '../../../../models/paiement.model';
import * as bootstrap from 'bootstrap';

@Component({
  selector: 'app-projet-factures',
  templateUrl: './projet-factures.component.html',
  styleUrls: ['./projet-factures.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class ProjetFacturesComponent implements OnInit {
  @Input() projectId: number = 0;
  factures: Facture[] = [];
  isLoading = false;
  selectedFile: File | null = null;
  
  // Payment related properties
  selectedFacture: Facture | null = null;
  newPaiement: Paiement = {
    montant: 0,
    datePaiement: new Date().toISOString().split('T')[0],
    methodePaiement: 'CARTE',
    reference: '',
    status: 'COMPLETE',
    facture: null
  };
  paiementModal: bootstrap.Modal | null = null;

  constructor(
    private factureService: FactureService,
    private paiementService: PaiementService,
    private toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.loadFactures();
  }  loadFactures() {
    this.isLoading = true;
    // Initialiser avec un tableau vide
    this.factures = [];
    
    // Utiliser la méthode spécifique pour récupérer les factures par projet
    this.factureService.getFacturesByProjectId(this.projectId).subscribe({
      next: (data) => {
        if (data) {
          this.factures = data;
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading factures', error);
        this.isLoading = false;
        this.toastr.warning('Impossible de charger les factures - vérifiez la connexion au serveur');
      }
    });
  }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  uploadInvoiceFile() {
    if (!this.selectedFile) {
      this.toastr.warning('Veuillez sélectionner un fichier');
      return;
    }

    this.isLoading = true;
    this.factureService.uploadInvoiceFile(this.selectedFile).subscribe({
      next: (response) => {
        this.toastr.success(`Fichier traité avec succès: ${response.invoiceCount} factures importées`);
        this.loadFactures();
        this.selectedFile = null;
      },
      error: (error) => {
        console.error('Error uploading invoice file', error);
        this.isLoading = false;
        this.toastr.error('Erreur lors du téléchargement du fichier');
      }
    });
  }

  downloadTemplate() {
    this.factureService.downloadTemplate().subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'factures-template.csv';
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Error downloading template', error);
        this.toastr.error('Erreur lors du téléchargement du modèle');
      }
    });
  }

  deleteFacture(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette facture ?')) {
      this.isLoading = true;
      this.factureService.deleteFacture(id).subscribe({
        next: () => {
          this.toastr.success('Facture supprimée avec succès');
          this.loadFactures();
        },
        error: (error) => {
          console.error('Error deleting facture', error);
          this.isLoading = false;
          this.toastr.error('Erreur lors de la suppression de la facture');
        }
      });
    }
  }

  openPaiementModal(facture: Facture): void {
    this.selectedFacture = facture;
    this.newPaiement.montant = facture.montant;
    this.newPaiement.facture = facture;
    this.newPaiement.datePaiement = new Date().toISOString().split('T')[0];
    
    // Open the modal using Bootstrap's Modal API
    setTimeout(() => {
      const modalElement = document.getElementById('createPaiementModal');
      if (modalElement) {
        this.paiementModal = new bootstrap.Modal(modalElement);
        this.paiementModal.show();
      }
    });
  }

  isFormValid(): boolean {
    return this.newPaiement.methodePaiement !== undefined && 
           this.newPaiement.methodePaiement.length > 0 && 
           this.newPaiement.reference !== undefined &&
           this.newPaiement.reference.length > 0 &&
           this.newPaiement.datePaiement !== undefined &&
           this.newPaiement.datePaiement.length > 0;
  }

  createPaiement(): void {
    if (!this.selectedFacture) {
      this.toastr.error('Aucune facture sélectionnée.');
      return;
    }

    this.isLoading = true;
    this.paiementService.createPaiement(this.newPaiement).subscribe({
      next: (response) => {
        this.toastr.success('Paiement créé avec succès');
        
        // Update the invoice status to PAYEE
        if (this.selectedFacture && this.selectedFacture.id) {
          this.selectedFacture.statut = 'PAYEE';
          this.factureService.updateFacture(this.selectedFacture.id, this.selectedFacture).subscribe({
            next: () => {
              this.loadFactures();
              if (this.paiementModal) {
                this.paiementModal.hide();
              }
            },
            error: (error) => {
              console.error('Error updating invoice status', error);
              this.toastr.warning('Le paiement a été créé mais le statut de la facture n\'a pas été mis à jour');
            }
          });
        }
        
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error creating payment', error);
        this.toastr.error('Erreur lors de la création du paiement');
        this.isLoading = false;
      }
    });
  }
}
