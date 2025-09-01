import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SidebarComponent } from './sidebar.component';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

describe('SidebarComponent', () => {
  let component: SidebarComponent;
  let fixture: ComponentFixture<SidebarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SidebarComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of({ get: (key: string) => null }),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SidebarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('toggle', () => {
    it('should set expanded to false and componentClass to empty string when expanded is true', () => {
      // Given
      component.expanded = true;
      component.componentClass = 'unexpanded';

      // When
      component.toggle();

      // Then
      expect(component.expanded).toBe(false);
      expect(component.componentClass).toBe('');
    });

    it('should set expanded to true and componentClass to "unexpanded" when expanded is false', () => {
      // Given
      component.expanded = false;
      component.componentClass = '';

      // When
      component.toggle();

      // Then
      expect(component.expanded).toBe(true);
      expect(component.componentClass).toBe('unexpanded');
    });
  });
});
