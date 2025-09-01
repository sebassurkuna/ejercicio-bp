import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment.local';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + '/reportes';

    getCurrentDate(): string {
      const now = new Date();
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }

  generateReport(clientId: string, fechaDesde: string = "2025-01-01", fechaHasta: string = this.getCurrentDate()) {
    const params = new HttpParams()
      .set('clienteId', clientId)
      .set('fechaDesde', fechaDesde)
      .set('fechaHasta', fechaHasta)
      .set('formato', 'pdf');
    return this.http.get(this.apiUrl, { params });
  }
}
