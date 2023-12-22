import { IEmployees } from 'app/entities/employees/employees.model';
import { IJobHistory } from 'app/entities/job-history/job-history.model';
import { ILocations } from 'app/entities/locations/locations.model';

export interface IDepartments {
  id: number;
  departmentId?: number | null;
  departmentName?: string | null;
  employees?: IEmployees[] | null;
  jobHistories?: IJobHistory[] | null;
  deptLocFk?: ILocations | null;
  depMgrFk?: IEmployees | null;
}

export type NewDepartments = Omit<IDepartments, 'id'> & { id: null };
