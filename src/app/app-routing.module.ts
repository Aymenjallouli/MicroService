import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BackHomeComponent } from './back/back-home/back-home.component';

import { ReclamationComponent } from './components/Reclamationn/reclamation/reclamation.component';
import { ReclamationListComponent } from './components/Reclamationn/reclamation-list/reclamation-list.component';
import { ReclamationEditComponent } from './components/Reclamationn/reclamation-edit/reclamation-edit.component';
import { ReponseComponent } from './components/Reponsee/reponse/reponse.component';
import { ReponseListComponent } from './components/Reponsee/reponse-list/reponse-list.component';
import { AuthGuard } from './guard/auth.guard';
import { UserManagementComponentComponent } from './back/UserComponents/user-management-component/user-management-component.component';
import { NavBarComponent } from './back/nav-bar/nav-bar.component';
import { NavBarFrontComponent } from './front/nav-bar-front/nav-bar-front.component';
import { LoginComponent } from './login/login.component';
import { ProfileBackComponent } from './back/UserComponents/profile-back/profile-back.component';
import { UpdateUserComponent } from './back/UserComponents/update-user/update-user.component';

import { AllFilesComponent } from './back/all-files/all-files.component';
import { ReclamationStatistiqueComponent } from './components/Reclamationn/reclamation-statistique/reclamation-statistique.component';
import { ReclamationListUserComponent } from './components/Reclamationn/reclamation-list-user/reclamation-list-user.component';
import { ReclamationEditUserComponent } from './components/Reclamationn/reclamation-edit-user/reclamation-edit-user.component';
import { ReponseEditComponent } from './components/Reponsee/reponse-edit/reponse-edit.component';
import { BackComponent } from './back/back.component';
import { AdminAuthGuard } from './guard/admin-auth.guard';
import { UserAuthGuard } from './guard/user-auth.guard';
import { HomeComponent } from './front/home/home.component';
import { FrontComponent } from './front/front.component';
import { ProfileComponent } from './profile/profile.component';
import { UpdateProfileComponent } from './updateprofile/updateprofile.component';
import { AddArticleComponent } from './back/article/add-article/add-article.component';
import { ModifyArticleComponent } from './back/article/modify-article/modify-article.component';
import { AddBasketComponent } from './back/basket/add-basket/add-basket.component';
import { ModifyBasketComponent } from './back/basket/modify-basket/modify-basket.component';
import { AddOrderComponent } from './back/order/add-order/add-order.component';
import { ArticlesComponent } from './front/articles/articles.component';
import { ArticleComponent } from './back/article/article.component';
import { OrderSummaryComponent } from './front/order-summary/order-summary.component';
import { BasketComponent } from './back/basket/basket.component';
import { OrderComponent } from './back/order/order.component';
import { ProjectsComponent } from './back/projects/projects.component';
import { WeatherComponent } from './Services/weather.component';
import { EtudesComponent } from './back/etudes/etudes.component';
import { ProjectFrontListComponent } from './front/project-front-list/project-front-list.component';
import { WeatherFrontComponent } from './front/weather-front/weather-front.component';
import { EtudesFrontComponent } from './front/etudes-front/etudes-front.component';
import { PredictionComponent } from './prediction/prediction.component';
import { BuildingCostComponent } from './building-cost/building-cost.component';
import { ProjetFinanceComponent } from './back/projects/projet-finance';

const routes: Routes = [
  { path: '', component: LoginComponent },
  {
    path: 'admins',
    canActivate: [AdminAuthGuard],
    component: BackComponent,
    data: { roles: ['admin'] },
    children: [
      { path: '', component: BackHomeComponent },
      { path: 'add', component: UserManagementComponentComponent },
      { path: 'profile', component: ProfileComponent },
      { path: 'update/:email', component: UpdateUserComponent },
      { path: 'reclamationList', component: ReclamationListComponent },
      { path: 'reclamationEdit/:id', component: ReclamationEditComponent },
      { path: 'reponse/:id', component: ReponseComponent },
      { path: 'reponseList', component: ReponseListComponent },
      { path: 'reponse', component: ReponseComponent },

      { path: 'filepostulation', component: AllFilesComponent },
      {
        path: 'reclamationStatistique',
        component: ReclamationStatistiqueComponent,
      },
      { path: 'reponseEdit', component: ReponseEditComponent },
      { path: 'reponseEdit/:id', component: ReponseEditComponent },
      { path: 'reclamationListUser', component: ReclamationListUserComponent },
      {
        path: 'reclamationEditUser/:id',
        component: ReclamationEditUserComponent,
      },
      {
        path: 'reclamationStatistique',
        component: ReclamationStatistiqueComponent,
      },

      { path: 'articles', component: ArticleComponent },
      { path: 'addArticle', component: AddArticleComponent },
      { path: 'modifyArticle/:id', component: ModifyArticleComponent },

      { path: 'baskets', component: BasketComponent },
      { path: 'addBasket', component: AddBasketComponent },
      { path: 'modifyBasket/:id', component: ModifyBasketComponent },

      { path: 'orders', component: OrderComponent },
      { path: 'addOrder', component: AddOrderComponent },
      { path: 'projects', component: ProjectsComponent },
      { path: 'projects/:id/finance', component: ProjetFinanceComponent },

      { path: 'etudes', component: EtudesComponent },
      { path: 'weather', component: WeatherComponent },
      // { path: 'modifyOrder/:id', component: AddOrderComponent },
    ],
  },
  {
    path: 'user',
    canActivate: [UserAuthGuard],
    component: FrontComponent,
    data: { roles: ['user'] },
    children: [
      { path: '', component: HomeComponent },
      { path: 'reclamation', component: ReclamationComponent },
      {
        path: 'profile',
        component: ProfileComponent,
        canActivate: [AuthGuard],
        data: { roles: ['user', 'admin'] },
      },
      { path: 'updateprofile/:email', component: UpdateProfileComponent },
      { path: 'reclamationListUser', component: ReclamationListUserComponent },
      {
        path: 'reclamationEditUser/:id',
        component: ReclamationEditUserComponent,
      },
      {
        path: 'addreclamation',
        component: ReclamationComponent,
      },
      {
        path: 'reclamationStatistique',
        component: ReclamationStatistiqueComponent,
      },

      { path: 'articles', component: ArticlesComponent },
      { path: 'orders', component: OrderSummaryComponent },
      {path: 'prediction', component: PredictionComponent},
      { path: 'projects', component: ProjectFrontListComponent },
      { path: 'weather', component: WeatherFrontComponent },
      { path: 'etudes', component: EtudesFrontComponent },
      {path: 'predictionprojet', component: BuildingCostComponent},
    ],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
