export interface Depense {
  id?: number;
  description: string;
  montant: number;
  date?: Date;
  fileName?: string;
  fileType?: string;
  statut?: 'EN_ATTENTE' | 'VALIDEE' | 'REJETEE';
  type: 'MATERIEL' | 'MAIN_DOEUVRE' | 'TRANSPORT' | 'AUTRES';
  idProjet: number;
  projectName?: string;
  projectDescription?: string;
  beneficiaire?: string;
}
