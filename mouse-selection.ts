function getSelection() {
  let selection = window.getSelection() as any;
  if (!selection.isCollapsed) {
    selection = trimSelection(selection);
  }
  return selection;
}

function expandSelection(range: Range) {
  if (!range || range.collapsed || range.toString().length <= 1) {
    return;
  }
  const moveStart = (offset) => range.setStart(range.startContainer, range.startOffset + offset);
  const moveEnd = (offset) => range.setEnd(range.endContainer, range.endOffset + offset);
  const wordMatcher = /\w/;
  const hasWordCharacterAtBeginning = range.toString().match(/^\w/);

  if (hasWordCharacterAtBeginning) {
    while (range.toString()[0].match(wordMatcher)) {
      try {
        moveStart(-1);
      } catch {
        return;
      }
    }
    moveStart(1);
  }

  const hasWordCharacterAtEnd = range.toString().match(/\w$/);

  if (hasWordCharacterAtEnd) {
    while (range.toString()[range.toString().length - 1].match(wordMatcher)) {
      try {
        moveEnd(1);
      } catch {
        return;
      }
    }

    moveEnd(-1);
  }

  return range;
}

function trimSelection(selection: Selection): Selection {
  const chars = selection.toString().split('');
  const backwardSelection = selection.anchorOffset > selection.focusOffset;
  const [oldStart, oldEnd] =  backwardSelection
    ? [selection.focusOffset, selection.anchorOffset]
    : [selection.anchorOffset, selection.focusOffset];
  const start = oldStart + chars.findIndex(c => c !== ' '); // right shifting
  const end = oldEnd - chars.reverse().findIndex(c => c !== ' '); // left shifting
  const range = new Range();
  try {
    range.setStart(selection.anchorNode, start);
    range.setEnd(selection.focusNode, end);
  } catch {
    return;
  }
  selection.removeRange(selection.getRangeAt(0));
  selection.addRange(range);
  return selection;
}

function emptyMouseSelection() {
  return {value: '',  start: -1, end: -1, coordinate: {}};
}
/**
 * src: http://jsfiddle.net/UuDpL/2/
 */
export function getMouseSelection(element: Element, refineCallback: (priorRange: Range) => string): any {
  const selection = getSelection();
  if (!selection) {
    return emptyMouseSelection();
  }
  let range = getSelection().getRangeAt(0);
  range = expandSelection(range);
  if (!range) {
    return emptyMouseSelection();
  }
  const priorRange = range.cloneRange();
  const value = range.toString().trim();
  priorRange.selectNodeContents(element);
  priorRange.setEnd(range.startContainer, range.startOffset);
  const priorText = refineCallback(priorRange);
  const start = priorText.length;
  const end = start + value.length;
  return {value, start, end, coordinate: getMouseCoordinate(range)};
}

function getMouseCoordinate(elem: any) {
  const clientRects = elem.getClientRects();
  const scrollTop = window.pageYOffset;
  const top = clientRects[clientRects.length - 1].bottom + scrollTop;
  const left = clientRects[clientRects.length - 1].right;
  return {top, left};
}

export function clearMouseSelection() {
  window.getSelection().removeAllRanges();
}
