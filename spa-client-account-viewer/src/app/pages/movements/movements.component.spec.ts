import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MovementsComponent } from './movements.component';
import { provideHttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

describe('MovementsComponent', () => {
  let component: MovementsComponent;
  let fixture: ComponentFixture<MovementsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MovementsComponent],
      providers: [
        provideHttpClient(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: (key: string) => {
                  if (key === 'client') return '123';
                  if (key === 'account') return '456';
                  return null;
                },
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MovementsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call getmovements when clientid and accountid exists', () => {
    jest
      .spyOn(component['_movementService'], 'getMovements')
      .mockReturnValue(of([]));

    expect(component.movements).toEqual([]);
  });
});
