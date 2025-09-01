import { TestBed } from '@angular/core/testing';

import { ReportService } from './report.service';
import { provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { environment } from '../../../environments/environment.local';

describe('ReportService', () => {
  let service: ReportService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ReportService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call generateReport with correct parameters', () => {
    const clientId = '123';
    const accountId = '456';

    jest.spyOn(service, 'generateReport').mockReturnValue(of({}));

    service.generateReport(clientId, accountId);

    expect(service.generateReport).toHaveBeenCalledWith(clientId, accountId);
  });

  it('should return the current date in YYYY-MM-DD format', () => {
    const currentDate = service.getCurrentDate();
    const regex = /^\d{4}-\d{2}-\d{2}$/;
    expect(currentDate).toMatch(regex);
  });

  it('should call the API with the correct parameters', (done) => {
    const mockData = {
      pdfBase64: 'mockedBase64String',
    };
    const clientId = '123';

    service.generateReport(clientId).subscribe((data) => {
      expect(data).toEqual(mockData);
      done();
    });

    const req = httpTesting.expectOne(
      (request) => request.url === `${environment.apiUrl}/reportes`
    );
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('clienteId')).toBe(clientId);
    expect(req.request.params.get('fechaDesde')).toBe('2025-01-01');
    expect(req.request.params.get('fechaHasta')).toBe(service.getCurrentDate());
    expect(req.request.params.get('formato')).toBe('pdf');

    req.flush(mockData);
  });
});
