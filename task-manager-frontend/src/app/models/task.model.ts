export interface Task {
    id?: number;
    title: string;
    description: string;
    dueDate: string;
    priority: 'HIGH' | 'MEDIUM' | 'LOW';
    status: 'TODO' | 'IN_PROGRESS' | 'DONE';
    userId?: number;
}
