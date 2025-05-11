import { ProjectTasksComponent } from './back/projects/project-tasks/project-tasks.component';
import { TaskDetailsComponent } from './back/projects/task-details/task-details.component';

export const routes = [
  {
    path: 'projects/:id/tasks',
    component: ProjectTasksComponent
  },
  {
    path: 'projects/:id/tasks/:taskId',
    component: TaskDetailsComponent
  },
  {
    path: 'admins/projects/:id/tasks',
    component: ProjectTasksComponent
  },
  {
    path: 'admins/projects/:id/tasks/:taskId',
    component: TaskDetailsComponent
  }
];