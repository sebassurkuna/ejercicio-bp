import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment.local';
import { Movement } from '../../interfaces/movement.interface';
import { of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MovementService {
  private readonly _http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + '/movimientos';

  getMovements(clientId:string, accountId: string, fechaDesde: string = "2025-01-01", fechaHasta: string = "2025-12-31", page: number = 0, size: number = 20) {
    const params = new HttpParams()
      .set('numeroCuenta', accountId)
      .set('clienteId', clientId)
      .set('fechaDesde', fechaDesde)
      .set('fechaHasta', fechaHasta)
      .set('page', page.toString())
      .set('size', size.toString());
    return this._http.get<Movement[]>(`${this.apiUrl}`, { params });
  }

}