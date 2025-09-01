import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataTableComponent } from './data-table.component';

describe('DataTableComponent', () => {
  let component: DataTableComponent;
  let fixture: ComponentFixture<DataTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DataTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DataTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  describe('paginatedData getter', () => {
    it('given data and default page/pageSize, when accessed, then returns first page of data', () => {
      component.data = Array.from({ length: 50 }, (_, i) => ({ id: i }));
      component.pageSize = 20;
      component.page = 1;
      expect(component.paginatedData.length).toBe(20);
      expect(component.paginatedData[0].id).toBe(0);
      expect(component.paginatedData[19].id).toBe(19);
    });

    it('given data and page 2, when accessed, then returns correct slice', () => {
      component.data = Array.from({ length: 50 }, (_, i) => ({ id: i }));
      component.pageSize = 20;
      component.page = 2;
      expect(component.paginatedData.length).toBe(20);
      expect(component.paginatedData[0].id).toBe(20);
      expect(component.paginatedData[19].id).toBe(39);
    });

    it('given data and last page, when accessed, then returns remaining items', () => {
      component.data = Array.from({ length: 45 }, (_, i) => ({ id: i }));
      component.pageSize = 20;
      component.page = 3;
      expect(component.paginatedData.length).toBe(5);
      expect(component.paginatedData[0].id).toBe(40);
    });
  });

  describe('totalPages getter', () => {
    it('given 45 items and pageSize 20, when accessed, then returns 3', () => {
      component.data = Array.from({ length: 45 }, (_, i) => ({ id: i }));
      component.pageSize = 20;
      expect(component.totalPages).toBe(3);
    });

    it('given 0 items, when accessed, then returns 0', () => {
      component.data = [];
      component.pageSize = 20;
      expect(component.totalPages).toBe(0);
    });
  });

  describe('getNestedValue', () => {
    it('given nested object and valid path, when called, then returns nested value', () => {
      const obj = { a: { b: { c: 42 } } };
      expect(component.getNestedValue(obj, 'a.b.c')).toBe(42);
    });

    it('given nested object and invalid path, when called, then returns undefined', () => {
      const obj = { a: { b: { c: 42 } } };
      expect(component.getNestedValue(obj, 'a.x.c')).toBeUndefined();
    });

    it('given flat object and single key, when called, then returns value', () => {
      const obj = { foo: 123 };
      expect(component.getNestedValue(obj, 'foo')).toBe(123);
    });
  });

  describe('goToPage', () => {
    beforeEach(() => {
      component.data = Array.from({ length: 50 }, (_, i) => ({ id: i }));
      component.pageSize = 20;
    });

    it('given valid page number, when called, then sets page', () => {
      component.goToPage(2);
      expect(component.page).toBe(2);
    });

    it('given page number less than 1, when called, then does not change page', () => {
      component.page = 1;
      component.goToPage(0);
      expect(component.page).toBe(1);
    });

    it('given page number greater than totalPages, when called, then does not change page', () => {
      component.page = 1;
      component.goToPage(10);
      expect(component.page).toBe(1);
    });
  });

  describe('toggleMenu', () => {
    it('given openMenuIndex is null, when called with index, then sets openMenuIndex to index', () => {
      component.openMenuIndex = null;
      component.toggleMenu(3);
      expect(component.openMenuIndex).toBe(3);
    });

    it('given openMenuIndex is same as index, when called, then sets openMenuIndex to null', () => {
      component.openMenuIndex = 2;
      component.toggleMenu(2);
      expect(component.openMenuIndex).toBeNull();
    });

    it('given openMenuIndex is different, when called, then sets openMenuIndex to new index', () => {
      component.openMenuIndex = 1;
      component.toggleMenu(4);
      expect(component.openMenuIndex).toBe(4);
    });
  });

  describe('closeMenu', () => {
    it('given openMenuIndex is set, when called, then sets openMenuIndex to null', () => {
      component.openMenuIndex = 5;
      component.closeMenu();
      expect(component.openMenuIndex).toBeNull();
    });
  });

  describe('onEdit', () => {
    it('given editFn is defined, when called, then calls editFn with id and closes menu', () => {
      const editFn = jest.fn();
      component.editFn = editFn;
      component.openMenuIndex = 1;
      component.onEdit('abc');
      expect(editFn).toHaveBeenCalledWith('abc');
      expect(component.openMenuIndex).toBeNull();
    });

    it('given editFn is undefined, when called, then does not throw and closes menu', () => {
      component.editFn = undefined;
      component.openMenuIndex = 2;
      expect(() => component.onEdit('id')).not.toThrow();
      expect(component.openMenuIndex).toBeNull();
    });
  });

  describe('onDelete', () => {
    it('given deleteFn is defined, when called, then calls deleteFn with id and closes menu', () => {
      const deleteFn = jest.fn();
      component.deleteFn = deleteFn;
      component.openMenuIndex = 1;
      component.onDelete('xyz');
      expect(deleteFn).toHaveBeenCalledWith('xyz');
      expect(component.openMenuIndex).toBeNull();
    });

    it('given deleteFn is undefined, when called, then does not throw and closes menu', () => {
      component.deleteFn = undefined;
      component.openMenuIndex = 2;
      expect(() => component.onDelete('id')).not.toThrow();
      expect(component.openMenuIndex).toBeNull();
    });
  });

});
