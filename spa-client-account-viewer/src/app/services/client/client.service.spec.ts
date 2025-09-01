import { TestBed } from '@angular/core/testing';

import { ClientService } from './client.service';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { environment } from '../../../environments/environment.local';

describe('ClientService', () => {
  let service: ClientService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ClientService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call the API with the correct parameters', (done) => {
    const mockData = {
      id: '123',
      name: 'John Doe',
    };
    const clientId = '123';

    service.getClientById(clientId).subscribe((data) => {
      expect(data).toEqual(mockData);
      done();
    });

    const req = httpTesting.expectOne(
      (request) => request.url === `${environment.apiUrl}/clientes/${clientId}`
    );
    expect(req.request.method).toBe('GET');

    req.flush(mockData);
  });

  // Additional tests for createClient, updateClient, deleteClient can be added here
  it('should create a client', (done) => {
    const mockData = {
      id: '123',
      personaId: '456',
      persona: {
        id: '456',
        nombre: 'Marianela',
        apellido: 'Montalvo',
        genero: 'FEMENINO',
        fechaNacimiento: '1990-03-15',
        identificacion: '1102233344',
        telefono: '098123456',
        direccion: 'Av. Amazonas 123',
        estado: true,
        createdAt: '2024-01-01',
        updatedAt: '2024-01-01',
      },
      username: 'marianela.m',
      password: 'Passw0rd!',
      estado: true,
      createdAt: '2024-01-01',
      updatedAt: '2024-01-01',
    };

    service.createClient(mockData).subscribe((data) => {
      expect(data).toEqual(mockData);
      done();
    });

    const req = httpTesting.expectOne(
      (request) => request.url === `${environment.apiUrl}/clientes`
    );
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockData);

    req.flush(mockData);
  });

  it('should update a client', (done) => {
    const mockData = {
      id: '123',
      personaId: '456',
      persona: {
        id: '456',
        nombre: 'Marianela',
        apellido: 'Montalvo',
        genero: 'FEMENINO',
        fechaNacimiento: '1990-03-15',
        identificacion: '1102233344',
        telefono: '098123456',
        direccion: 'Av. Amazonas 123',
        estado: true,
        createdAt: '2024-01-01',
        updatedAt: '2024-01-01',
      },
      username: 'marianela.m',
      password: 'Passw0rd!',
      estado: true,
      createdAt: '2024-01-01',
      updatedAt: '2024-01-01',
    };

    service.updateClient(mockData).subscribe((data) => {
      expect(data).toEqual(mockData);
      done();
    });

    const req = httpTesting.expectOne(
      (request) =>
        request.url === `${environment.apiUrl}/clientes/${mockData.id}`
    );
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(mockData);

    req.flush(mockData);
  });

  it('should delete a client', (done) => {
    const clientId = '123';

    service.deleteClient(clientId).subscribe((data) => {
      expect(data).toEqual({});
      done();
    });

    const req = httpTesting.expectOne(
      (request) => request.url === `${environment.apiUrl}/clientes/${clientId}`
    );
    expect(req.request.method).toBe('DELETE');

    req.flush({});
  });
});
