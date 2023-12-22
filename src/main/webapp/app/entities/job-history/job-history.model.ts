import dayjs from 'dayjs/esm';
import { IDepartments } from 'app/entities/departments/departments.model';
import { IJobs } from 'app/entities/jobs/jobs.model';

export interface IJobHistory {
  id: number;
  employId?: number | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  jhistDepFk?: IDepartments | null;
  jhistJob?: IJobs | null;
}

export type NewJobHistory = Omit<IJobHistory, 'id'> & { id: null };
