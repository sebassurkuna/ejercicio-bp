export interface Movement {
	id: string;
	cuentaId: string;
	fecha: string;
	tipo: 'DEBITO' | 'CREDITO' | string;
	valor: number;
	saldoPostMovimiento: number;
	createdAt: string;
}
