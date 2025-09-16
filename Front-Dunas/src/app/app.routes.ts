import { Routes } from '@angular/router';
import {HomeComponent} from './home/home.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {BackofficeComponent} from './backoffice/backoffice.component';
import {LoginComponent} from './login/login.component';
import {UnauthorizedComponent} from './unauthorized/unauthorized.component';
import {AuthGuard} from './guard/auth.guard';
import {LoggedGuard} from './guard/logged.guard';
import {RegisterComponent} from './register/register.component';
import {ReservationComponent} from './reservation/reservation.component';
import {CreateReservationComponent} from './create-reservation/create-reservation.component';

export const routes: Routes = [
  {path: "", component: HomeComponent},
  {path: "dashboard", component: DashboardComponent, canActivate: [AuthGuard]},
  {path: "backoffice", component:BackofficeComponent,canActivate: [AuthGuard], data : { role: 'manager', resource: 'manager-backoffice' } },
  {path: "login", component: LoginComponent, canActivate: [LoggedGuard]},
  {path: "admin/backoffice", component: BackofficeComponent,canActivate: [AuthGuard], data : { role: 'admin', resource: 'admin-backoffice' } },
  {path: "unauthorized", component: UnauthorizedComponent},
  {path: "register", component: RegisterComponent, canActivate: [LoggedGuard]},
  {path: "reservations", component: ReservationComponent},
  {path: "create-reservation", component: CreateReservationComponent}
];
