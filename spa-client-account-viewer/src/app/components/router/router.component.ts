import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-router',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './router.component.html',
  styleUrl: './router.component.scss'
})
export class RouterComponent {

}
