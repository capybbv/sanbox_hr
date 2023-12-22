import dayjs from 'dayjs/esm';
import { IDepartments } from 'app/entities/departments/departments.model';
import { IJobs } from 'app/entities/jobs/jobs.model';

export interface IEmployees {
  id: number;
  employeeId?: number | null;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  phoneNumber?: string | null;
  hireDate?: dayjs.Dayjs | null;
  salary?: number | null;
  commissionPct?: number | null;
  employees?: IEmployees[] | null;
  departments?: IDepartments[] | null;
  empManagerFk?: IEmployees | null;
  empDeptFk?: IDepartments | null;
  empJobFk?: IJobs | null;
}

export type NewEmployees = Omit<IEmployees, 'id'> & { id: null };
