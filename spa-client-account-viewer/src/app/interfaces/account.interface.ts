export interface Account {
	id: string;
	clienteId: string;
	numeroCuenta: number;
	tipo: 'CORRIENTE' | 'AHORROS' | string;
	saldoInicial: number;
	saldoActual: number;
	estado: boolean;
	createdAt: string;
	updatedAt: string;
}
