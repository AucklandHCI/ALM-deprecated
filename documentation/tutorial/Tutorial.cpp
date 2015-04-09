#include <Application.h>
#include <Button.h>
#include <List.h>
#include <Window.h>

#include "Area.h"
#include "BALMLayout.h"
#include "OperatorType.h"
#include "XTab.h"
#include "YTab.h"


using namespace BALM;

class TutorialWindow : public BWindow {
public:
	TutorialWindow(BRect frame) 
		: BWindow(frame, "Tutorial", B_TITLED_WINDOW, B_QUIT_ON_WINDOW_CLOSE)
	{
		fLS = new BALMLayout();
		
		SetLayout(fLS);
				
		button = new BButton("Hello, World!");
		
		XTab* x1 = fLS->AddXTab();
		XTab* x2 = fLS->AddXTab();
		YTab* y1 = fLS->AddYTab();
		YTab* y2 = fLS->AddYTab();

		// Create an area for the button in the layout
		Area* a = fLS->AddArea(x1, y1, x2, y2, button);

		// Constrain the button to be at least 150x150
		a->SetMinContentSize(new BSize(150, 150));

		// Constrain the padding to be the same size on all edges
		fLS->AddConstraint(1.0, x1, -1.0, fLS->Left(), -1.0, fLS->Right(), 1.0, x2, OperatorType(EQ), 0.0);
		fLS->AddConstraint(1.0, x1, -1.0, fLS->Left(), -1.0, y1, 1.0, fLS->Top(), OperatorType(EQ), 0.0);
		fLS->AddConstraint(1.0, x1, -1.0, fLS->Left(), -1.0, fLS->Bottom(), 1.0, y2, OperatorType(EQ), 0.0);
		
		Show();
	}
	
private:
	BALMLayout* fLS;
	BButton* button;
};


class Tutorial : public BApplication {
public:
	Tutorial() 
		: BApplication("application/x-vnd.haiku.tutorial") 
	{
		BRect frameRect;
		frameRect.Set(100, 100, 392, 366);
		TutorialWindow* theWindow = new TutorialWindow(frameRect);
	}
};


int
main()
{
	Tutorial tutorial;
	tutorial.Run();
	return 0;
}


