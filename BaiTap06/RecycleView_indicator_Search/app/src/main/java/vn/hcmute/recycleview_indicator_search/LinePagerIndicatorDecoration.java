package vn.hcmute.recycleview_indicator_search;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LinePagerIndicatorDecoration extends RecyclerView.ItemDecoration {

    private final Paint mPaint = new Paint();
    private final int mIndicatorHeight = (int) (Resources.getSystem().getDisplayMetrics().density * 16);
    private final int mIndicatorStrokeWidth = (int) (Resources.getSystem().getDisplayMetrics().density * 2);
    private final int mIndicatorItemLength = (int) (Resources.getSystem().getDisplayMetrics().density * 16);
    private final int mIndicatorItemPadding = (int) (Resources.getSystem().getDisplayMetrics().density * 4);
    private final int mIndicatorActiveColor = 0xFFFFFFFF;
    private final int mIndicatorInactiveColor = 0x66FFFFFF;

    public LinePagerIndicatorDecoration() {
        mPaint.setStrokeWidth(mIndicatorStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int itemCount = parent.getAdapter().getItemCount();

        // center horizontally, calculate width and subtract half from center
        float totalLength = mIndicatorItemLength * itemCount;
        float paddingBetweenItems = Math.max(0, itemCount - 1) * mIndicatorItemPadding;
        float indicatorTotalWidth = totalLength + paddingBetweenItems;
        float indicatorStartX = (parent.getWidth() - indicatorTotalWidth) / 2F;

        // center vertically in the allotted space
        float indicatorPosY = parent.getHeight() - mIndicatorHeight / 2F;

        drawInactiveIndicators(c, indicatorStartX, indicatorPosY, itemCount);

        // find active page (which should be highlighted)
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        if (firstVisibleItemPosition == RecyclerView.NO_POSITION) {
            return;
        }

        // find offset of active page (if the user is scrolling)
        final View activeChild = layoutManager.findViewByPosition(firstVisibleItemPosition);
        int left = activeChild.getLeft();
        int width = activeChild.getWidth();
        int right = activeChild.getRight();

        // on swipe the active item will be positioned from [-width, 0] 
        // to translate that to a percentage
        float progress = mPaint.getStrokeWidth() / 2 + (left * -1) / (float) width;

        drawHighlights(c, indicatorStartX, indicatorPosY, firstVisibleItemPosition, progress);
    }

    private void drawInactiveIndicators(Canvas c, float indicatorStartX, float indicatorPosY, int itemCount) {
        mPaint.setColor(mIndicatorInactiveColor);

        // width of item indicator including padding
        final float itemWidth = mIndicatorItemLength + mIndicatorItemPadding;

        float start = indicatorStartX;
        for (int i = 0; i < itemCount; i++) {
            c.drawLine(start, indicatorPosY, start + mIndicatorItemLength, indicatorPosY, mPaint);
            start += itemWidth;
        }
    }

    private void drawHighlights(Canvas c, float indicatorStartX, float indicatorPosY, int highlightPosition, float progress) {
        mPaint.setColor(mIndicatorActiveColor);

        final float itemWidth = mIndicatorItemLength + mIndicatorItemPadding;

        if (progress == 0F) {
            // no swipe, draw a normal indicator
            float highlightStart = indicatorStartX + itemWidth * highlightPosition;
            c.drawLine(highlightStart, indicatorPosY, highlightStart + mIndicatorItemLength, indicatorPosY, mPaint);
        } else {
            float highlightStart = indicatorStartX + itemWidth * highlightPosition;
            // calculate partial highlight
            float partialLength = mIndicatorItemLength * progress;

            // draw the cut off highlight
            c.drawLine(highlightStart + partialLength, indicatorPosY, highlightStart + mIndicatorItemLength, indicatorPosY, mPaint);

            // draw the highlight overlapping to the next item as well
            if (highlightPosition < getItemCount() - 1) {
                highlightStart += itemWidth;
                c.drawLine(highlightStart, indicatorPosY, highlightStart + partialLength, indicatorPosY, mPaint);
            }
        }
    }

    private int getItemCount() {
        return 9; // Number of items in your recyclerview
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = mIndicatorHeight;
    }
}
