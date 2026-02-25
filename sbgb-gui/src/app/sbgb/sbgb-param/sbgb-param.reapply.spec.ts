import {SbgbComparisonService} from '../sbgb-comparison.service';
import {of, Subject} from 'rxjs';
import {SbgbParamComponent} from './sbgb-param.component';
import {SbgbPageActions} from '../state/sbgb.actions';
import {StructuralChangeChoice} from '../sbgb-structural-change-dialog/sbgb-structural-change-dialog.component';
import {selectCurrentSbgb, selectErrorMessage, selectInfoMessage, selectRenders} from '../state/sbgb.selectors';

describe('SbgbParamComponent — reapply renders with new base (Option B)', () => {
  let component: SbgbParamComponent;
  let mockStore: any;
  let mockSnackBar: any;
  let mockDialog: any;
  let dialogAfterClosed$: Subject<StructuralChangeChoice | undefined>;
  let renders$: Subject<any>;

  const twoRenders = [
    {id: 'r1', note: 3, back: '#111', middle: '#222', fore: '#333', backThreshold: 0.3, middleThreshold: 0.7, interpolationType: 'LINEAR', transparentBackground: false, thumbnail: null, baseStructureId: 'b1', description: 'd1'},
    {id: 'r2', note: 5, back: '#aaa', middle: '#bbb', fore: '#ccc', backThreshold: 0.4, middleThreshold: 0.8, interpolationType: 'SMOOTHSTEP', transparentBackground: true, thumbnail: null, baseStructureId: 'b1', description: 'd2'}
  ];

  beforeEach(() => {
    dialogAfterClosed$ = new Subject();
    renders$ = new Subject();

    mockStore = {
      select: jest.fn().mockImplementation((selector: any) => {
        if (selector === selectRenders) return renders$.asObservable();
        if (selector === selectCurrentSbgb) return new Subject().asObservable();
        if (selector === selectInfoMessage) return new Subject().asObservable();
        if (selector === selectErrorMessage) return new Subject().asObservable();
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
    renders$.next(twoRenders);
  });

  afterEach(() => {
    component.ngOnDestroy();
  });

  it('should dispatch rateSbgb for each render with current base params and render cosmetics when REAPPLY chosen', () => {
    component.baseForm.get('seed')!.setValue(9999);
    dialogAfterClosed$.next(StructuralChangeChoice.REAPPLY);

    expect(mockStore.dispatch).toHaveBeenCalledTimes(2);

    const firstCall = mockStore.dispatch.mock.calls[0][0];
    expect(firstCall.type).toBe(SbgbPageActions.rateSbgb.type);
    expect(firstCall.note).toBe(3);
    expect(firstCall.sbgb.imageColor.back).toBe('#111');
    expect(firstCall.sbgb.imageStructure.seed).toBe(9999);

    const secondCall = mockStore.dispatch.mock.calls[1][0];
    expect(secondCall.type).toBe(SbgbPageActions.rateSbgb.type);
    expect(secondCall.note).toBe(5);
    expect(secondCall.sbgb.imageColor.back).toBe('#aaa');
    expect(secondCall.sbgb.imageColor.interpolationType).toBe('SMOOTHSTEP');
  });
});
