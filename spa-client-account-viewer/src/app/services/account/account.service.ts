import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment.local';
import { Account } from '../../interfaces/account.interface';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + '/cuentas';

  getAccounts(clientId: string, page: number = 0, size: number = 20) {
    const params = new HttpParams()
      .set('clienteId', clientId)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Account[]>(this.apiUrl, { params });
  }
}
