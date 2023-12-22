import { IDepartments, NewDepartments } from './departments.model';

export const sampleWithRequiredData: IDepartments = {
  id: 13347,
  departmentId: 5092,
};

export const sampleWithPartialData: IDepartments = {
  id: 23920,
  departmentId: 26836,
};

export const sampleWithFullData: IDepartments = {
  id: 8485,
  departmentId: 18790,
  departmentName: 'hence posh gadzooks',
};

export const sampleWithNewData: NewDepartments = {
  departmentId: 9334,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
