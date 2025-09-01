import { Component, inject } from '@angular/core';
import { PageHeaderComponent } from '../../components/page-header/page-header.component';
import { MovementService } from '../../services/movement/movement.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Movement } from '../../interfaces/movement.interface';
import { DataTableColumn } from '../../interfaces/data-table.interface';
import { DataTableComponent } from '../../components/data-table/data-table.component';

@Component({
  selector: 'app-movements',
  standalone: true,
  imports: [PageHeaderComponent, DataTableComponent],
  templateUrl: './movements.component.html',
  styleUrl: './movements.component.scss'
})
export class MovementsComponent {
  private readonly _movementService = inject(MovementService);
  private readonly _activatedRoute = inject(ActivatedRoute);
  
  movements: Movement[] = [];
  columns: DataTableColumn[] = [
      { key: 'fecha', label: 'Fecha' },
      { key: 'tipo', label: 'Tipo' },
      { key: 'valor', label: 'Valor' },
      { key: 'saldoPostMovimiento', label: 'Saldo Actual' }
    ];

  constructor() {
    const clientId = this._activatedRoute.snapshot.paramMap.get('client');
    const accountId = this._activatedRoute.snapshot.paramMap.get('account');
    if (clientId && accountId) {
      this._movementService.getMovements(clientId, accountId).subscribe({
        next: (movements) => {
          this.movements = movements;
        },
        error: (error) => {
          console.error('Error fetching movements:', error);
        }
      });
    }
  }
}
