import { Routes } from '@angular/router';
import { ClientsComponent } from './pages/clients/clients.component';
import { AccountsComponent } from './pages/accounts/accounts.component';
import { ReportsComponent } from './pages/reports/reports.component';
import { MovementsComponent } from './pages/movements/movements.component';
import { ClientFormComponent } from './pages/client-form/client-form.component';
import { RouterComponent } from './components/router/router.component';

export const routes: Routes = [
  { path: 'clients', component: RouterComponent, children: [
    { path: '', component: ClientsComponent },
    { path: 'create', component: ClientFormComponent },
    { path: 'edit/:id', component: ClientFormComponent }
  ]},
  { path: 'accounts/:id', component: AccountsComponent },
  { path: 'movements/:client/:account', component: MovementsComponent },
  { path: 'reports', component: ReportsComponent }
];
