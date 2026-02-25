import {SbgbComparisonService} from '../sbgb-comparison.service';
import {of, Subject} from 'rxjs';
import {SbgbParamComponent} from './sbgb-param.component';
import {SbgbPageActions} from '../state/sbgb.actions';
import {StructuralChangeChoice} from '../sbgb-structural-change-dialog/sbgb-structural-change-dialog.component';
import {selectCurrentSbgb, selectErrorMessage, selectInfoMessage, selectRenders} from '../state/sbgb.selectors';

describe('SbgbParamComponent — structural change detection', () => {
  let component: SbgbParamComponent;
  let mockStore: any;
  let mockSnackBar: any;
  let mockDialog: any;
  let dialogAfterClosed$: Subject<StructuralChangeChoice | undefined>;
  let renders$: Subject<any>;
  let infoMessage$: Subject<string>;
  let errorMessage$: Subject<string>;
  let currentSbgb$: Subject<any>;

  beforeEach(() => {
    dialogAfterClosed$ = new Subject();
    renders$ = new Subject();
    infoMessage$ = new Subject();
    errorMessage$ = new Subject();
    currentSbgb$ = new Subject();

    mockStore = {
      select: jest.fn().mockImplementation((selector: any) => {
        if (selector === selectRenders) return renders$.asObservable();
        if (selector === selectCurrentSbgb) return currentSbgb$.asObservable();
        if (selector === selectInfoMessage) return infoMessage$.asObservable();
        if (selector === selectErrorMessage) return errorMessage$.asObservable();
        return of(null);
      }),
      selectSignal: jest.fn().mockReturnValue(() => null),
      dispatch: jest.fn()
    };
    mockSnackBar = {open: jest.fn()};
    mockDialog = {
      open: jest.fn().mockReturnValue({afterClosed: () => dialogAfterClosed$.asObservable()})
    };
    const mockActions$ = {pipe: jest.fn().mockReturnValue(of())};
    component = new SbgbParamComponent(mockSnackBar, mockStore, mockActions$ as any, mockDialog, new SbgbComparisonService());
    component.ngOnInit();
  });

  afterEach(() => {
    component.ngOnDestroy();
  });

  describe('when baseForm changes with existing renders', () => {
    beforeEach(() => {
      renders$.next([
        {id: 'r1', note: 3, back: '#000', middle: '#fff', fore: '#aaa', backThreshold: 0.3, middleThreshold: 0.7, interpolationType: 'LINEAR', transparentBackground: false, thumbnail: null, baseStructureId: 'b1', description: 'd1'},
        {id: 'r2', note: 4, back: '#111', middle: '#222', fore: '#333', backThreshold: 0.4, middleThreshold: 0.8, interpolationType: 'LINEAR', transparentBackground: false, thumbnail: null, baseStructureId: 'b1', description: 'd2'}
      ]);
    });

    it('should open the structural change dialog', () => {
      component.baseForm.get('seed')!.setValue(9999);

      expect(mockDialog.open).toHaveBeenCalled();
    });

    it('should dispatch deleteRender for each render when CLEAR is chosen', () => {
      component.baseForm.get('seed')!.setValue(9999);
      dialogAfterClosed$.next(StructuralChangeChoice.CLEAR);

      expect(mockStore.dispatch).toHaveBeenCalledWith(SbgbPageActions.deleteRender({renderId: 'r1'}));
      expect(mockStore.dispatch).toHaveBeenCalledWith(SbgbPageActions.deleteRender({renderId: 'r2'}));
    });

    it('should restore baseForm snapshot when CANCEL is chosen', () => {
      const seedBefore = component.baseForm.get('seed')!.value;
      component.baseForm.get('seed')!.setValue(9999);
      dialogAfterClosed$.next(StructuralChangeChoice.CANCEL);

      expect(component.baseForm.get('seed')!.value).toBe(seedBefore);
    });
  });

  describe('when cosmeticForm changes', () => {
    beforeEach(() => {
      renders$.next([
        {id: 'r1', note: 3, back: '#000', middle: '#fff', fore: '#aaa', backThreshold: 0.3, middleThreshold: 0.7, interpolationType: 'LINEAR', transparentBackground: false, thumbnail: null, baseStructureId: 'b1', description: 'd1'}
      ]);
    });

    it('should not open the structural change dialog', () => {
      component.cosmeticForm.get('backgroundColor')!.setValue('#ff0000');

      expect(mockDialog.open).not.toHaveBeenCalled();
    });
  });

  describe('when baseForm changes with no existing renders', () => {
    beforeEach(() => {
      renders$.next([]);
    });

    it('should not open the structural change dialog', () => {
      component.baseForm.get('seed')!.setValue(9999);

      expect(mockDialog.open).not.toHaveBeenCalled();
    });
  });
});
