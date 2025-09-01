import { Component, inject } from '@angular/core';
import { PageHeaderComponent } from '../../components/page-header/page-header.component';
import { ClientService } from '../../services/client/client.service';
import { Client } from '../../interfaces/client.interface';
import { DataTableComponent } from '../../components/data-table/data-table.component';
import { DataTableColumn } from '../../interfaces/data-table.interface';
import { Router } from '@angular/router';
import { ReportService } from '../../services/report/report.service';

@Component({
  selector: 'app-clients',
  standalone: true,
  imports: [PageHeaderComponent, DataTableComponent],
  templateUrl: './clients.component.html',
  styleUrl: './clients.component.scss'
})
export class ClientsComponent {
  private readonly _clientService = inject(ClientService);
  private readonly _router = inject(Router);
  private readonly _reportService = inject(ReportService);

  clients: Client[] = [];
  columns: DataTableColumn[] = [
    { key: 'username', label: 'Username' },
    { key: 'persona.nombre', label: 'Nombre' },
    { key: 'persona.apellido', label: 'Apellido' },
    { key: 'persona.identificacion', label: 'Identificación' },
  ];
  customActions = [
    { label: 'Ver cuentas', fn: (row: any) => this._router.navigate(['/accounts', row.id]) },
    { label: 'Generar reporte', fn: (row: any) => this._generateReport(row) },
  ];

  constructor() {
    this._loadClients();
  }

  navigateToCreate() {
    this._router.navigate(['/clients/create']);
  }

  navigateToEdit(id: string) {
    this._router.navigate(['/clients/edit', id]);
  }

  deleteClient(id: string) {
    this._clientService.deleteClient(id).subscribe(() => {
      this._loadClients();
    });
  }

  searchContentByUsername(username: string) {
    this._clientService.getClients().subscribe({
      next: (data) => {
        this.clients = username !== '' ? data.filter(client => client.username.includes(username)) : data;
      }
    });
  }

  private _generateReport(row: Client) {
    this._reportService.generateReport(row.id).subscribe({
      next: (data: any) => {
        const link = document.createElement('a');
        link.href = 'data:application/pdf;base64,' + data.pdfBase64;
        link.download = row.persona.nombre + ' ' + row.persona.apellido + ' - Reporte de Movimientos.pdf';
        link.click();
      },
      error: (error) => {
        console.error('Error generating report:', error);
      }
    });
  }

  private _loadClients() {
    this._clientService.getClients().subscribe({
      next: (data) => {
        this.clients = data;
      },
      error: (error) => {
        console.error('Error loading clients:', error);
      }
    });
  }
}
