import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IDepartments } from 'app/entities/departments/departments.model';
import { DepartmentsService } from 'app/entities/departments/service/departments.service';
import { IJobs } from 'app/entities/jobs/jobs.model';
import { JobsService } from 'app/entities/jobs/service/jobs.service';
import { IEmployees } from '../employees.model';
import { EmployeesService } from '../service/employees.service';
import { EmployeesFormService } from './employees-form.service';

import { EmployeesUpdateComponent } from './employees-update.component';

describe('Employees Management Update Component', () => {
  let comp: EmployeesUpdateComponent;
  let fixture: ComponentFixture<EmployeesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let employeesFormService: EmployeesFormService;
  let employeesService: EmployeesService;
  let departmentsService: DepartmentsService;
  let jobsService: JobsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), EmployeesUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(EmployeesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EmployeesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    employeesFormService = TestBed.inject(EmployeesFormService);
    employeesService = TestBed.inject(EmployeesService);
    departmentsService = TestBed.inject(DepartmentsService);
    jobsService = TestBed.inject(JobsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Employees query and add missing value', () => {
      const employees: IEmployees = { id: 456 };
      const empManagerFk: IEmployees = { id: 9740 };
      employees.empManagerFk = empManagerFk;

      const employeesCollection: IEmployees[] = [{ id: 11807 }];
      jest.spyOn(employeesService, 'query').mockReturnValue(of(new HttpResponse({ body: employeesCollection })));
      const additionalEmployees = [empManagerFk];
      const expectedCollection: IEmployees[] = [...additionalEmployees, ...employeesCollection];
      jest.spyOn(employeesService, 'addEmployeesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ employees });
      comp.ngOnInit();

      expect(employeesService.query).toHaveBeenCalled();
      expect(employeesService.addEmployeesToCollectionIfMissing).toHaveBeenCalledWith(
        employeesCollection,
        ...additionalEmployees.map(expect.objectContaining),
      );
      expect(comp.employeesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Departments query and add missing value', () => {
      const employees: IEmployees = { id: 456 };
      const empDeptFk: IDepartments = { id: 8220 };
      employees.empDeptFk = empDeptFk;

      const departmentsCollection: IDepartments[] = [{ id: 7785 }];
      jest.spyOn(departmentsService, 'query').mockReturnValue(of(new HttpResponse({ body: departmentsCollection })));
      const additionalDepartments = [empDeptFk];
      const expectedCollection: IDepartments[] = [...additionalDepartments, ...departmentsCollection];
      jest.spyOn(departmentsService, 'addDepartmentsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ employees });
      comp.ngOnInit();

      expect(departmentsService.query).toHaveBeenCalled();
      expect(departmentsService.addDepartmentsToCollectionIfMissing).toHaveBeenCalledWith(
        departmentsCollection,
        ...additionalDepartments.map(expect.objectContaining),
      );
      expect(comp.departmentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Jobs query and add missing value', () => {
      const employees: IEmployees = { id: 456 };
      const empJobFk: IJobs = { id: 14099 };
      employees.empJobFk = empJobFk;

      const jobsCollection: IJobs[] = [{ id: 14545 }];
      jest.spyOn(jobsService, 'query').mockReturnValue(of(new HttpResponse({ body: jobsCollection })));
      const additionalJobs = [empJobFk];
      const expectedCollection: IJobs[] = [...additionalJobs, ...jobsCollection];
      jest.spyOn(jobsService, 'addJobsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ employees });
      comp.ngOnInit();

      expect(jobsService.query).toHaveBeenCalled();
      expect(jobsService.addJobsToCollectionIfMissing).toHaveBeenCalledWith(jobsCollection, ...additionalJobs.map(expect.objectContaining));
      expect(comp.jobsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const employees: IEmployees = { id: 456 };
      const empManagerFk: IEmployees = { id: 7100 };
      employees.empManagerFk = empManagerFk;
      const empDeptFk: IDepartments = { id: 5586 };
      employees.empDeptFk = empDeptFk;
      const empJobFk: IJobs = { id: 16230 };
      employees.empJobFk = empJobFk;

      activatedRoute.data = of({ employees });
      comp.ngOnInit();

      expect(comp.employeesSharedCollection).toContain(empManagerFk);
      expect(comp.departmentsSharedCollection).toContain(empDeptFk);
      expect(comp.jobsSharedCollection).toContain(empJobFk);
      expect(comp.employees).toEqual(employees);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEmployees>>();
      const employees = { id: 123 };
      jest.spyOn(employeesFormService, 'getEmployees').mockReturnValue(employees);
      jest.spyOn(employeesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ employees });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: employees }));
      saveSubject.complete();

      // THEN
      expect(employeesFormService.getEmployees).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(employeesService.update).toHaveBeenCalledWith(expect.objectContaining(employees));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEmployees>>();
      const employees = { id: 123 };
      jest.spyOn(employeesFormService, 'getEmployees').mockReturnValue({ id: null });
      jest.spyOn(employeesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ employees: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: employees }));
      saveSubject.complete();

      // THEN
      expect(employeesFormService.getEmployees).toHaveBeenCalled();
      expect(employeesService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEmployees>>();
      const employees = { id: 123 };
      jest.spyOn(employeesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ employees });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(employeesService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareEmployees', () => {
      it('Should forward to employeesService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(employeesService, 'compareEmployees');
        comp.compareEmployees(entity, entity2);
        expect(employeesService.compareEmployees).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareDepartments', () => {
      it('Should forward to departmentsService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(departmentsService, 'compareDepartments');
        comp.compareDepartments(entity, entity2);
        expect(departmentsService.compareDepartments).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareJobs', () => {
      it('Should forward to jobsService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(jobsService, 'compareJobs');
        comp.compareJobs(entity, entity2);
        expect(jobsService.compareJobs).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
