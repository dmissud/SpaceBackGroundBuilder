import {of, Subject} from 'rxjs';
import {SbgbParamComponent} from './sbgb-param.component';
import {SbgbPageActions} from '../state/sbgb.actions';

describe('SbgbParamComponent — renders logic', () => {
  let component: SbgbParamComponent;
  let mockStore: any;
  let mockSnackBar: any;
  let currentSbgb$: Subject<any>;
  let renders$: Subject<any>;
  let infoMessage$: Subject<string>;
  let errorMessage$: Subject<string>;

  beforeEach(() => {
    currentSbgb$ = new Subject();
    renders$ = new Subject();
    infoMessage$ = new Subject();
    errorMessage$ = new Subject();

    mockStore = {
      select: jest.fn().mockImplementation((selector: any) => {
        const name = selector?.projector?.toString() ?? '';
        if (name.includes('renders')) return renders$.asObservable();
        if (name.includes('sbgb')) return currentSbgb$.asObservable();
        if (name.includes('infoMessage')) return infoMessage$.asObservable();
        if (name.includes('errorMessage')) return errorMessage$.asObservable();
        return of(null);
      }),
      selectSignal: jest.fn().mockReturnValue(() => null),
      dispatch: jest.fn()
    };

    mockSnackBar = {open: jest.fn()};

    const mockActions$ = {pipe: jest.fn().mockReturnValue(of())};
    component = new SbgbParamComponent(mockSnackBar, mockStore, mockActions$ as any);
  });

  afterEach(() => {
    component.ngOnDestroy();
  });

  describe('deleteRenderById', () => {
    it('should dispatch deleteRender action with the given render id', () => {
      component.deleteRenderById('render-42');

      expect(mockStore.dispatch).toHaveBeenCalledWith(
        SbgbPageActions.deleteRender({renderId: 'render-42'})
      );
    });
  });

  describe('loadRendersForBaseId', () => {
    it('should dispatch loadRendersForBase action with the given base id', () => {
      component.loadRendersForBaseId('base-123');

      expect(mockStore.dispatch).toHaveBeenCalledWith(
        SbgbPageActions.loadRendersForBase({baseId: 'base-123'})
      );
    });
  });
});
