import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IEmployees } from 'app/entities/employees/employees.model';
import { EmployeesService } from 'app/entities/employees/service/employees.service';
import { ILocations } from 'app/entities/locations/locations.model';
import { LocationsService } from 'app/entities/locations/service/locations.service';
import { IDepartments } from '../departments.model';
import { DepartmentsService } from '../service/departments.service';
import { DepartmentsFormService } from './departments-form.service';

import { DepartmentsUpdateComponent } from './departments-update.component';

describe('Departments Management Update Component', () => {
  let comp: DepartmentsUpdateComponent;
  let fixture: ComponentFixture<DepartmentsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let departmentsFormService: DepartmentsFormService;
  let departmentsService: DepartmentsService;
  let employeesService: EmployeesService;
  let locationsService: LocationsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), DepartmentsUpdateComponent],
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
      .overrideTemplate(DepartmentsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DepartmentsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    departmentsFormService = TestBed.inject(DepartmentsFormService);
    departmentsService = TestBed.inject(DepartmentsService);
    employeesService = TestBed.inject(EmployeesService);
    locationsService = TestBed.inject(LocationsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Employees query and add missing value', () => {
      const departments: IDepartments = { id: 456 };
      const depMgrFk: IEmployees = { id: 10068 };
      departments.depMgrFk = depMgrFk;

      const employeesCollection: IEmployees[] = [{ id: 16573 }];
      jest.spyOn(employeesService, 'query').mockReturnValue(of(new HttpResponse({ body: employeesCollection })));
      const additionalEmployees = [depMgrFk];
      const expectedCollection: IEmployees[] = [...additionalEmployees, ...employeesCollection];
      jest.spyOn(employeesService, 'addEmployeesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ departments });
      comp.ngOnInit();

      expect(employeesService.query).toHaveBeenCalled();
      expect(employeesService.addEmployeesToCollectionIfMissing).toHaveBeenCalledWith(
        employeesCollection,
        ...additionalEmployees.map(expect.objectContaining),
      );
      expect(comp.employeesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Locations query and add missing value', () => {
      const departments: IDepartments = { id: 456 };
      const deptLocFk: ILocations = { id: 14818 };
      departments.deptLocFk = deptLocFk;

      const locationsCollection: ILocations[] = [{ id: 22140 }];
      jest.spyOn(locationsService, 'query').mockReturnValue(of(new HttpResponse({ body: locationsCollection })));
      const additionalLocations = [deptLocFk];
      const expectedCollection: ILocations[] = [...additionalLocations, ...locationsCollection];
      jest.spyOn(locationsService, 'addLocationsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ departments });
      comp.ngOnInit();

      expect(locationsService.query).toHaveBeenCalled();
      expect(locationsService.addLocationsToCollectionIfMissing).toHaveBeenCalledWith(
        locationsCollection,
        ...additionalLocations.map(expect.objectContaining),
      );
      expect(comp.locationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const departments: IDepartments = { id: 456 };
      const depMgrFk: IEmployees = { id: 28864 };
      departments.depMgrFk = depMgrFk;
      const deptLocFk: ILocations = { id: 20046 };
      departments.deptLocFk = deptLocFk;

      activatedRoute.data = of({ departments });
      comp.ngOnInit();

      expect(comp.employeesSharedCollection).toContain(depMgrFk);
      expect(comp.locationsSharedCollection).toContain(deptLocFk);
      expect(comp.departments).toEqual(departments);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDepartments>>();
      const departments = { id: 123 };
      jest.spyOn(departmentsFormService, 'getDepartments').mockReturnValue(departments);
      jest.spyOn(departmentsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ departments });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: departments }));
      saveSubject.complete();

      // THEN
      expect(departmentsFormService.getDepartments).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(departmentsService.update).toHaveBeenCalledWith(expect.objectContaining(departments));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDepartments>>();
      const departments = { id: 123 };
      jest.spyOn(departmentsFormService, 'getDepartments').mockReturnValue({ id: null });
      jest.spyOn(departmentsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ departments: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: departments }));
      saveSubject.complete();

      // THEN
      expect(departmentsFormService.getDepartments).toHaveBeenCalled();
      expect(departmentsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDepartments>>();
      const departments = { id: 123 };
      jest.spyOn(departmentsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ departments });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(departmentsService.update).toHaveBeenCalled();
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

    describe('compareLocations', () => {
      it('Should forward to locationsService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(locationsService, 'compareLocations');
        comp.compareLocations(entity, entity2);
        expect(locationsService.compareLocations).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
