export interface Person {
	id: string;
	nombre: string;
	apellido: string;
	genero: 'MASCULINO' | 'FEMENINO' | string;
	fechaNacimiento: string;
	identificacion: string;
	telefono: string;
	direccion: string;
	estado: boolean;
	createdAt: string;
	updatedAt: string;
}

export interface Client {
	id: string;
	personaId: string;
	persona: Person;
	username: string;
	password?: string;
	estado: boolean;
	createdAt: string;
	updatedAt: string;
}