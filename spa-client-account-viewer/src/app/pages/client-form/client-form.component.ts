import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ClientService } from '../../services/client/client.service';

@Component({
  selector: 'app-client-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './client-form.component.html',
  styleUrl: './client-form.component.scss'
})
export class ClientFormComponent {

  private readonly _fb = inject(FormBuilder);
  private readonly _activatedRoute = inject(ActivatedRoute);
  private readonly _clientService = inject(ClientService);
  private readonly _router = inject(Router);

  generos = [
    { value: 'MASCULINO', label: 'Masculino' },
    { value: 'FEMENINO', label: 'Femenino' }
  ];

  title: string = '';
  form!: FormGroup;
  idCliente: string = "";
  isEditMode = false;

  constructor() {
    this._buildForm();
    this._checkEditMode();
  }
  
  onSubmit() {
    console.log(this.form.value);
    if (this.form.valid) {
      if (this.isEditMode) {
        this._clientService.updateClient({ ...this.form.value, id: this.idCliente }).subscribe(() => {
          alert('Cliente actualizado correctamente');
          this._router.navigate(['/clients']);
        });
      } else {
        this._clientService.createClient(this.form.value).subscribe(() => {
          alert('Cliente creado correctamente');
          this._router.navigate(['/clients']);
        });
      }
    } else {
      this.form.markAllAsTouched();
      alert('Por favor, completa todos los campos');
    }
  }
  
  private _checkEditMode() {
    this._activatedRoute.paramMap.subscribe(params => {
      this.idCliente = params.get('id') ?? '';
      this.isEditMode = !!this.idCliente;
    });
    if (this.isEditMode) {
      this.title = 'Edición de Cliente existente';
      this._clientService.getClientById(this.idCliente).subscribe(client => {
        this.form.patchValue({
          persona: {
            nombre: client.persona.nombre,
            apellido: client.persona.apellido,
            genero: client.persona.genero,
            fechaNacimiento: client.persona.fechaNacimiento,
            identificacion: client.persona.identificacion,
            telefono: client.persona.telefono,
            direccion: client.persona.direccion
          },
          username: client.username,
          password: client.password,
          estado: client.estado
        });
      });
    } else {
      this.title = 'Registro de cliente nuevo';
    }
  }

  private _buildForm() {
    this.form = this._fb.group({
      persona: this._fb.group({
        nombre: ['', Validators.required],
        apellido: ['', Validators.required],
        genero: ['', Validators.required],
        fechaNacimiento: ['', Validators.required],
        identificacion: ['', Validators.required],
        telefono: ['', Validators.required],
        direccion: ['', Validators.required],
      }),
      username: ['', Validators.required],
      password: ['', Validators.required],
      estado: [true, Validators.required]
    });
  }
}
