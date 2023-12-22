import { IEmployees } from 'app/entities/employees/employees.model';
import { IJobHistory } from 'app/entities/job-history/job-history.model';

export interface IJobs {
  id: number;
  jobId?: number | null;
  jobTitle?: string | null;
  minSalary?: number | null;
  maxSalary?: number | null;
  employees?: IEmployees[] | null;
  jobHistories?: IJobHistory[] | null;
}

export type NewJobs = Omit<IJobs, 'id'> & { id: null };
