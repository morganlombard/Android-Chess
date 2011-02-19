package games.chess;

import games.chess.chessCore.cell;
import games.chess.chessCore.piece;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

public class boardView extends View
{

	// selected acts as an indicator as to  whether a cell has been selected
	// if so, the next screen event will be the selection of the move to cell
	boolean pieceSelected;
	
	// Yzero is the top point for the board, as in board is placed at [0, Yzero]
	private int fromX, fromY, toX, toY, Yzero, width;
	private chess game;
	private chessCore core;
	private Resources res;
	private Canvas canvas;
	
	private int getBoardX(int x)
	{
		return x * width / 8;
	}
	
	private int getBoardY(int y)
	{
		return y * width / 8 + Yzero;
	}
	
	private void drawBoard(cell[][] board) {
		canvas.restore();
		Drawable boardImg = res.getDrawable(R.drawable.board);
		width =  canvas.getWidth(); 
		Yzero =  (int)(canvas.getHeight() - width) / 2;
		boardImg.setBounds(0, Yzero, width, width + Yzero);
		boardImg.draw(canvas);
		
		for(int x = 0; x < 8; x++)
		{
			for(int y = 0; y < 8; y++)
			{
				piece piece = board[x][y].getPiece();
				if(piece != null)
				{
					Drawable figure = res.getDrawable(piece.getImageResource());
					figure.setBounds(getBoardX(x), getBoardY(y), getBoardX(x) + width/8, getBoardY(y) + width /8);
					figure.draw(canvas);
				}
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		
		// X and Y have to be integers 0-7
		// so what we need to do for x is x / (full width / 8)
		// y is similar but y - top space / (width of board / 8)
		if(!pieceSelected)
		{	
			fromX = (int)(event.getX() / (width / 8.0));
			fromY = (int)((event.getY() - Yzero) / (width / 8.0));
			if( fromX < 0 || fromX > 7 || fromY < 0 || fromY > 7)
				return true;
		}
		else
		{
			toX = (int)(event.getX() / (width / 8.0));
			toY = (int)((event.getY() - Yzero) / (width / 8.0));
			// moving to the same place shouldn't be an issue, i don't think
			// TODO: verify this
			if( toX < 0 || toX > 7 || toY < 0 || toY > 7 || (toX == fromX && toY == fromY));
			else
			{
				try
				{
					if(core.move(fromX, fromY, toX,toY));
					this.drawBoard(core.getBoard());
				}catch( Exception ex)
				{
					pieceSelected = false;
					return true;
					// TODO: handle this bizsnatch
				}
			}
		}
		pieceSelected = !pieceSelected;
		return true;
	}

	// Draw Chess board and set up logic connections
	// TODO: probably not here. Set up to and from cell selection
	// this may be related to view available moves. 
	@Override
	protected void onDraw(Canvas canvas) 
	{		
		this.canvas = canvas;
		pieceSelected = false;
		fromX =-1;
		fromY = -1;
		toX = -1;
		toY = -1;
		// TODO: change this constructor to be meaningful
		core = new chessCore();
		res = getResources();
		this.drawBoard(core.getBoard());
	}
	
	boardView(Context context)
	{
		super(context);
		game = (chess)context;
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	
}
