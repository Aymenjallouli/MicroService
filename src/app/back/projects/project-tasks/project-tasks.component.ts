import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Task } from '../../../models/task.model';
import { TaskService } from '../../../Services/task.service';

@Component({
  selector: 'app-project-tasks',
  templateUrl: './project-tasks.component.html',
  styleUrls: ['./project-tasks.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class ProjectTasksComponent implements OnInit {
  projectId: number = 0;
  projectName: string = '';
  tasks: Task[] = [];
  isLoading: boolean = false;
  selectedFile: File | null = null;
  
  newTask: Task = {
    title: '',
    description: '',
    status: 'UNREACHED',
    priority: 'MEDIUM',
    startDate: new Date().toISOString().split('T')[0],
    dueDate: new Date().toISOString().split('T')[0],
    projectId: 0
  };

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
    private toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.projectId = +params['id'];
      this.newTask.projectId = this.projectId;
      this.loadTasks();
      this.loadProjectDetails();
    });
  }

  loadProjectDetails() {
    // Récupérer les détails du projet depuis le service de projet ou depuis la route
    this.projectName = `Projet #${this.projectId}`;
  }

  loadTasks() {
    this.isLoading = true;
    this.tasks = [];
    
    this.taskService.getTasksByProject(this.projectId).subscribe({
      next: (data) => {
        if (data) {
          this.tasks = data;
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading tasks', error);
        this.isLoading = false;
        this.toastr.warning('Impossible de charger les tâches - vérifiez la connexion au serveur');
      }
    });
  }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  saveTask() {
    if (!this.newTask.title || !this.newTask.status || !this.newTask.priority) {
      this.toastr.warning('Veuillez remplir tous les champs obligatoires');
      return;
    }

    this.isLoading = true;
    this.taskService.createTask(this.newTask).subscribe({
      next: (result) => {
        if (this.selectedFile) {
          this.uploadTaskPhoto(result.id!);
        } else {
          this.finishSaving();
        }
      },
      error: (error) => {
        console.error('Error creating task', error);
        this.isLoading = false;
        this.toastr.error('Erreur lors de la création de la tâche');
      }
    });
  }

  uploadTaskPhoto(taskId: number) {
    if (this.selectedFile) {
      this.taskService.uploadTaskPhoto(taskId, this.selectedFile).subscribe({
        next: () => {
          this.finishSaving();
        },
        error: (error) => {
          console.error('Error uploading photo', error);
          this.isLoading = false;
          this.toastr.error("Erreur lors du téléchargement de la photo");
          this.loadTasks();  // Recharger les tâches malgré l'erreur de fichier
        }
      });
    }
  }

  finishSaving() {
    this.isLoading = false;
    this.toastr.success('Tâche ajoutée avec succès');
    this.newTask = {
      title: '',
      description: '',
      status: 'UNREACHED',
      priority: 'MEDIUM',
      startDate: new Date().toISOString().split('T')[0],
      dueDate: new Date().toISOString().split('T')[0],
      projectId: this.projectId
    };
    this.selectedFile = null;
    this.loadTasks();
  }

  deleteTask(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette tâche ?')) {
      this.isLoading = true;
      this.taskService.deleteTask(id).subscribe({
        next: () => {
          this.toastr.success('Tâche supprimée avec succès');
          this.loadTasks();
        },
        error: (error) => {
          console.error('Error deleting task', error);
          this.isLoading = false;
          this.toastr.error('Erreur lors de la suppression de la tâche');
        }
      });
    }
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'UNREACHED': return 'bg-secondary';
      case 'INPROGRESS': return 'bg-warning';
      case 'DONE': return 'bg-success';
      case 'OVERDUE': return 'bg-danger';
      default: return 'bg-light';
    }
  }

  getPriorityClass(priority: string): string {
    switch (priority) {
      case 'LOW': return 'bg-info';
      case 'MEDIUM': return 'bg-primary';
      case 'HIGH': return 'bg-danger';
      default: return 'bg-light';
    }
  }
}
