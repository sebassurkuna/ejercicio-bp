import { Component, HostBinding } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SIDEBAR_ITEMS } from '../../const/sidebar-items.const';
import { SidebarItem } from '../../interfaces/sidebar-item.interface';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
  @HostBinding('className') componentClass: string = "";

  sidebarItems: SidebarItem[] = SIDEBAR_ITEMS;
  expanded: boolean = true;

  toggle(): void {
    this.expanded = !this.expanded;
    if(this.expanded) { 
      this.componentClass = "unexpanded";
    } else {
      this.componentClass = ""; 
    }
  }
}
