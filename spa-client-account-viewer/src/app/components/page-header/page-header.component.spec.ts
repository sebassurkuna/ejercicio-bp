import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PageHeaderComponent } from './page-header.component';

describe('PageHeaderComponent', () => {
  let component: PageHeaderComponent;
  let fixture: ComponentFixture<PageHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PageHeaderComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PageHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit the input value when changeSearchTerm is called', () => {
    const testValue = 'busqueda';
    const event = { target: { value: testValue } } as unknown as Event;
    jest.spyOn(component.search, 'emit');

    component.changeSearchTerm(event);

    expect(component.search.emit).toHaveBeenCalledWith(testValue);
  });
});
