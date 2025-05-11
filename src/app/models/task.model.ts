export interface Task {
  id?: number;
  title: string;
  description?: string;
  status: 'UNREACHED' | 'INPROGRESS' | 'DONE' | 'OVERDUE';
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  startDate: string;
  dueDate: string;
  assigneeId?: number;
  projectId?: number;
  missionIds?: number[];
  imageUrl?: string;
  projectName?: string;
}
