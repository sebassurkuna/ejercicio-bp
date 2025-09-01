import { Component, inject } from '@angular/core';
import { PageHeaderComponent } from '../../components/page-header/page-header.component';
import { AccountService } from '../../services/account/account.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Account } from '../../interfaces/account.interface';
import { DataTableColumn } from '../../interfaces/data-table.interface';
import { DataTableComponent } from '../../components/data-table/data-table.component';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [PageHeaderComponent, DataTableComponent],
  templateUrl: './accounts.component.html',
  styleUrl: './accounts.component.scss',
})
export class AccountsComponent {
  private readonly _accountService = inject(AccountService);
  private readonly _activatedRoute = inject(ActivatedRoute);
  private readonly _router = inject(Router);

  clientId: string = '';
  accounts: Account[] = [];
  columns: DataTableColumn[] = [
    { key: 'numeroCuenta', label: 'Número de Cuenta' },
    { key: 'tipo', label: 'Tipo' },
    { key: 'saldoInicial', label: 'Saldo Inicial' },
    { key: 'saldoActual', label: 'Saldo Actual' }
  ];
  customActions = [
    {
      label: 'Ver movimientos',
      fn: (row: any) => this._router.navigate(['/movements', row.clienteId, row.numeroCuenta]),
    },
  ];

  constructor() {
    this._activatedRoute.params.subscribe((params) => {
      this.clientId = params['id'];
    });
    this._loadAccounts();
  }

  private _loadAccounts() {
    this._accountService.getAccounts(this.clientId).subscribe({
      next: (data) => {
        this.accounts = data;
      },
      error: (error) => {
        console.error('Error loading accounts:', error);
      },
    });
  }
}
