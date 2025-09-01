import { TestBed } from '@angular/core/testing';

import { MovementService } from './movement.service';
import { provideHttpClient } from '@angular/common/http';

describe('MovementService', () => {
  let service: MovementService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient()]
    });
    service = TestBed.inject(MovementService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
