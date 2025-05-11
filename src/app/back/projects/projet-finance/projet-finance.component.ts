import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Depense } from '../../../models/depense.model';
import { DepenseService } from '../../../Services/depense.service';
import { FactureService } from '../../../Services/facture.service';
import { ProjetFacturesComponent } from './projet-factures/projet-factures.component';
import { ProjetPaiementsComponent } from './projet-paiements/projet-paiements.component';

@Component({
  selector: 'app-projet-finance',
  templateUrl: './projet-finance.component.html',
  styleUrls: ['./projet-finance.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, ProjetFacturesComponent, ProjetPaiementsComponent]
})
export class ProjetFinanceComponent implements OnInit {
  projectId: number = 0;
  projectName: string = '';
  depenses: Depense[] = [];
  isLoading: boolean = false;
  activeTab: string = 'depenses';
  selectedFile: File | null = null;
  newDepense: Depense = {
    description: '',
    montant: 0,
    type: 'MATERIEL',
    idProjet: 0,
    beneficiaire: ''
  };

  constructor(
    private route: ActivatedRoute,
    private depenseService: DepenseService,
    private factureService: FactureService,
    private toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.projectId = +params['id'];
      this.newDepense.idProjet = this.projectId;
      this.loadDepenses();
      this.loadProjectDetails();
    });
  }
  loadProjectDetails() {
    // Définir un nom de projet par défaut en cas d'échec de chargement
    this.projectName = `Projet #${this.projectId}`;
    
    this.depenseService.getAllProjects().subscribe({
      next: (projects) => {
        if (projects && projects.length > 0) {
          const project = projects.find(p => p.id === this.projectId);
          if (project) {
            this.projectName = project.nomProjet || project.name || this.projectName;
          }
        }
      },
      error: (error) => {
        console.error('Error loading project details', error);
        this.toastr.warning('Détails du projet non disponibles, utilisation du nom par défaut');
      }
    });
  }
  loadDepenses() {
    this.isLoading = true;
    
    // Initialiser avec un tableau vide en cas d'erreur
    this.depenses = [];
    
    this.depenseService.getDepensesByProject(this.projectId).subscribe({
      next: (data) => {
        if (data) {
          this.depenses = data;
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading depenses', error);
        this.isLoading = false;
        this.toastr.warning('Impossible de charger les dépenses - vérifiez la connexion au serveur');
      }
    });
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  saveDepense() {
    if (!this.newDepense.description || !this.newDepense.montant || !this.newDepense.type) {
      this.toastr.warning('Veuillez remplir tous les champs obligatoires');
      return;
    }

    this.isLoading = true;
    this.depenseService.createDepense(this.newDepense).subscribe({
      next: (result) => {
        if (this.selectedFile) {
          this.uploadFile(result.id!);
        } else {
          this.finishSaving();
        }
      },
      error: (error) => {
        console.error('Error creating depense', error);
        this.isLoading = false;
        this.toastr.error('Erreur lors de la création de la dépense');
      }
    });
  }

  uploadFile(depenseId: number) {
    if (this.selectedFile) {
      this.depenseService.uploadFile(depenseId, this.selectedFile).subscribe({
        next: () => {
          this.finishSaving();
        },
        error: (error) => {
          console.error('Error uploading file', error);
          this.isLoading = false;
          this.toastr.error('Erreur lors du téléchargement du fichier');
          this.loadDepenses();  // Recharger les dépenses malgré l'erreur de fichier
        }
      });
    }
  }

  finishSaving() {
    this.isLoading = false;
    this.toastr.success('Dépense ajoutée avec succès');
    this.newDepense = {
      description: '',
      montant: 0,
      type: 'MATERIEL',
      idProjet: this.projectId,
      beneficiaire: ''
    };
    this.selectedFile = null;
    this.loadDepenses();
  }

  deleteDepense(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette dépense ?')) {
      this.isLoading = true;
      this.depenseService.deleteDepense(id).subscribe({
        next: () => {
          this.toastr.success('Dépense supprimée avec succès');
          this.loadDepenses();
        },
        error: (error) => {
          console.error('Error deleting depense', error);
          this.isLoading = false;
          this.toastr.error('Erreur lors de la suppression de la dépense');
        }
      });
    }
  }

  downloadFile(depenseId: number) {
    this.depenseService.downloadFile(depenseId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'depense-file';
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Error downloading file', error);
        this.toastr.error('Erreur lors du téléchargement du fichier');
      }
    });
  }
    generateFacture(depenseId: number) {
    this.isLoading = true;
    console.log(`Attempting to generate invoice for expense ID: ${depenseId}`);
    
    this.factureService.generateFromDepense(depenseId).subscribe({
      next: (facture) => {
        console.log('Facture generated successfully:', facture);
        this.isLoading = false;
        
        // Vérifier si la facture a un numéro
        const invoiceNumber = facture?.numero || 'Nouvelle facture';
        this.toastr.success(`Facture générée avec succès: ${invoiceNumber}`);
        
        // Recharger les factures et passer à l'onglet factures
        setTimeout(() => {
          this.setActiveTab('factures');
        }, 500);
      },
      error: (error) => {
        console.error('Error generating facture:', error);
        this.isLoading = false;
        
        // Afficher plus de détails sur l'erreur
        let errorMessage = 'Erreur lors de la génération de la facture';
        if (error.error && typeof error.error === 'string') {
          errorMessage += `: ${error.error}`;
        } else if (error.message) {
          errorMessage += `: ${error.message}`;
        }
        
        this.toastr.error(errorMessage);
      }
    });
  }
}
