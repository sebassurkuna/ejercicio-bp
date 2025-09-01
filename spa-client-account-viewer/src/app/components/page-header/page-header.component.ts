import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-page-header',
  standalone: true,
  imports: [],
  templateUrl: './page-header.component.html',
  styleUrl: './page-header.component.scss'
})
export class PageHeaderComponent {
  @Input() title: string = '';
  @Output() search = new EventEmitter<string>();
  @Output() create = new EventEmitter<void>();

  changeSearchTerm(event: Event) {
    const input = event.target as HTMLInputElement;
    this.search.emit(input.value);
  }
}
