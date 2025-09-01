import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DataTableColumn } from '../../interfaces/data-table.interface';

@Component({
  selector: 'app-data-table',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './data-table.component.html',
  styleUrl: './data-table.component.scss',
})
export class DataTableComponent {
  @Input() columns: Array<DataTableColumn> = [];
  @Input() data: any[] = [];
  @Input() editFn: ((id: string) => void) | undefined;
  @Input() deleteFn: ((id: string) => void) | undefined;

  pageSize: number = 20;
  page: number = 1;
  openMenuIndex: number | null = null;

  get paginatedData() {
    const start = (this.page - 1) * this.pageSize;
    return this.data.slice(start, start + this.pageSize);
  }

  get totalPages() {
    return Math.ceil(this.data.length / this.pageSize);
  }

  getNestedValue(obj: any, path: string): any {
    return path.split('.').reduce((acc, part) => acc && acc[part], obj);
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.page = page;
    }
  }

  toggleMenu(index: number) {
    this.openMenuIndex = this.openMenuIndex === index ? null : index;
  }

  closeMenu() {
    this.openMenuIndex = null;
  }

  onEdit(id: string) {
    this.editFn?.(id);
    this.closeMenu();
  }

  onDelete(id: string) {
    this.deleteFn?.(id);
    this.closeMenu();
  }
}
