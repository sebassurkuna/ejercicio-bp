import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment.local';
import { Client } from '../../interfaces/client.interface';
import { of } from 'rxjs';

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
    /*return of([
      {
        id: 'aedd227e-e751-4aac-8e5e-900e89c54000',
        personaId: '2c0091fd-f009-4a45-8903-77328e55c73b',
        persona: {
          id: '2c0091fd-f009-4a45-8903-77328e55c73b',
          nombre: 'Marianela',
          apellido: 'Montalvo',
          genero: 'FEMENINO',
          fechaNacimiento: '1990-03-15',
          identificacion: '1102233344',
          telefono: '098123456',
          direccion: 'Av. Amazonas 123',
          estado: true,
          createdAt: '2025-09-01T04:28:13.046688Z',
          updatedAt: '2025-09-01T04:28:13.046688Z',
        },
        username: 'marianela.m',
        estado: true,
        createdAt: '2025-09-01T04:28:13.046688Z',
        updatedAt: '2025-09-01T04:28:13.046688Z',
      },
      {
        id: '53953677-e23c-456f-859c-18b880530e2c',
        personaId: '43934314-65fb-421e-a8ef-4435b8dbaff7',
        persona: {
          id: '43934314-65fb-421e-a8ef-4435b8dbaff7',
          nombre: 'Luis',
          apellido: 'Lopez',
          genero: 'MASCULINO',
          fechaNacimiento: '2002-09-20',
          identificacion: '1727264846',
          telefono: '0962755721',
          direccion: 'Av. Amazonas 456',
          estado: true,
          createdAt: '2025-08-31T15:45:15.092717Z',
          updatedAt: '2025-08-31T15:49:11.183113Z',
        },
        username: 'sebas.san',
        estado: true,
        createdAt: '2025-08-31T15:45:15.092717Z',
        updatedAt: '2025-08-31T15:49:11.215526Z',
      },
    ]);*/
  }

  getClientById(id: string) {
    return this.http.get<Client>(`${this.apiUrl}/${id}`);
    /*return of({
        id: '53953677-e23c-456f-859c-18b880530e2c',
        personaId: '43934314-65fb-421e-a8ef-4435b8dbaff7',
        persona: {
          id: '43934314-65fb-421e-a8ef-4435b8dbaff7',
          nombre: 'Luis',
          apellido: 'Lopez',
          genero: 'MASCULINO',
          fechaNacimiento: '2002-09-20',
          identificacion: '1727264846',
          telefono: '0962755721',
          direccion: 'Av. Amazonas 456',
          estado: true,
          createdAt: '2025-08-31T15:45:15.092717Z',
          updatedAt: '2025-08-31T15:49:11.183113Z',
        },
        username: 'sebas.san',
        password: 'password123',
        estado: true,
        createdAt: '2025-08-31T15:45:15.092717Z',
        updatedAt: '2025-08-31T15:49:11.215526Z',
      })*/
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
