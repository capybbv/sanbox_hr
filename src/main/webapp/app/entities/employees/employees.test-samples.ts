import dayjs from 'dayjs/esm';

import { IEmployees, NewEmployees } from './employees.model';

export const sampleWithRequiredData: IEmployees = {
  id: 11831,
  employeeId: 4883,
  lastName: 'Glover',
  email: 'Amalia_Lehner@yahoo.com',
  hireDate: dayjs('2023-12-22T02:04'),
};

export const sampleWithPartialData: IEmployees = {
  id: 7504,
  employeeId: 3890,
  firstName: 'Salvatore',
  lastName: 'Langosh',
  email: 'Terrance_Kemmer@gmail.com',
  phoneNumber: 'whimsical geez',
  hireDate: dayjs('2023-12-21T20:21'),
};

export const sampleWithFullData: IEmployees = {
  id: 27291,
  employeeId: 21057,
  firstName: 'Lolita',
  lastName: 'Hilpert',
  email: 'Leda70@gmail.com',
  phoneNumber: 'reassemble prolong l',
  hireDate: dayjs('2023-12-21T07:43'),
  salary: 26852,
  commissionPct: 6379,
};

export const sampleWithNewData: NewEmployees = {
  employeeId: 31111,
  lastName: 'Simonis',
  email: 'Susana_Ebert@hotmail.com',
  hireDate: dayjs('2023-12-21T13:28'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
