import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment.local';
import { Client } from '../../interfaces/client.interface';

@Injectable({
  providedIn: 'root',
})
export class ClientService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + '/clientes';

  getClients(page: number = 0, size: number = 20) {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Client[]>(this.apiUrl, { params });
  }

  getClientById(id: string) {
    return this.http.get<Client>(`${this.apiUrl}/${id}`);
  }

  createClient(client: Client) {
    return this.http.post<Client>(this.apiUrl, client);
  }

  updateClient(client: Client) {
    return this.http.put<Client>(`${this.apiUrl}/${client.id}`, client);
  }

  deleteClient(id: string) {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
