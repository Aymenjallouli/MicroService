export interface Facture {
  id?: number;
  numero?: string;
  dateFacture?: Date;
  montant: number;
  statut?: string;
  description?: string;
  projectId: number;
  projectName?: string;
  projectDescription?: string;
}
