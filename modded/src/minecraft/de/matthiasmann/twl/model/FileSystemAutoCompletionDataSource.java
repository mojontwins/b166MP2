package de.matthiasmann.twl.model;

import java.util.ArrayList;

public class FileSystemAutoCompletionDataSource implements AutoCompletionDataSource {
	final FileSystemModel fsm;
	final FileSystemModel.FileFilter fileFilter;

	public FileSystemAutoCompletionDataSource(FileSystemModel fsm, FileSystemModel.FileFilter fileFilter) {
		if(fsm == null) {
			throw new NullPointerException("fsm");
		} else {
			this.fsm = fsm;
			this.fileFilter = fileFilter;
		}
	}

	public FileSystemModel getFileSystemModel() {
		return this.fsm;
	}

	public FileSystemModel.FileFilter getFileFilter() {
		return this.fileFilter;
	}

	public AutoCompletionResult collectSuggestions(String text, int cursorPos, AutoCompletionResult prev) {
		text = text.substring(0, cursorPos);
		int prefixLength = this.computePrefixLength(text);
		String prefix = text.substring(0, prefixLength);
		Object parent;
		if(prev instanceof FileSystemAutoCompletionDataSource.Result && prev.getPrefixLength() == prefixLength && prev.getText().startsWith(prefix)) {
			parent = ((FileSystemAutoCompletionDataSource.Result)prev).parent;
		} else {
			parent = this.fsm.getFile(prefix);
		}

		if(parent == null) {
			return null;
		} else {
			FileSystemAutoCompletionDataSource.Result result = new FileSystemAutoCompletionDataSource.Result(text, prefixLength, parent);
			this.fsm.listFolder(parent, result);
			return result.getNumResults() == 0 ? null : result;
		}
	}

	int computePrefixLength(String text) {
		String separator = this.fsm.getSeparator();
		int prefixLength = text.lastIndexOf(separator) + separator.length();
		if(prefixLength < 0) {
			prefixLength = 0;
		}

		return prefixLength;
	}

	class Result extends AutoCompletionResult implements FileSystemModel.FileFilter {
		final Object parent;
		final String nameFilter;
		final ArrayList results1 = new ArrayList();
		final ArrayList results2 = new ArrayList();

		public Result(String text, int prefixLength, Object parent) {
			super(text, prefixLength);
			this.parent = parent;
			this.nameFilter = text.substring(prefixLength).toUpperCase();
		}

		public boolean accept(FileSystemModel fsm, Object file) {
			FileSystemModel.FileFilter ff = FileSystemAutoCompletionDataSource.this.fileFilter;
			if(ff == null || ff.accept(fsm, file)) {
				int idx = this.getMatchIndex(fsm.getName(file));
				if(idx >= 0) {
					this.addName(fsm.getPath(file), idx);
				}
			}

			return false;
		}

		private int getMatchIndex(String partName) {
			return partName.toUpperCase().indexOf(this.nameFilter);
		}

		private void addName(String fullName, int matchIdx) {
			if(matchIdx == 0) {
				this.results1.add(fullName);
			} else if(matchIdx > 0) {
				this.results2.add(fullName);
			}

		}

		private void addFiltedNames(ArrayList results) {
			int i = 0;

			for(int n = results.size(); i < n; ++i) {
				String fullName = (String)results.get(i);
				int idx = this.getMatchIndex(fullName.substring(this.prefixLength));
				this.addName(fullName, idx);
			}

		}

		public int getNumResults() {
			return this.results1.size() + this.results2.size();
		}

		public String getResult(int idx) {
			int size1 = this.results1.size();
			return idx >= size1 ? (String)this.results2.get(idx - size1) : (String)this.results1.get(idx);
		}

		boolean canRefine(String text) {
			return this.prefixLength == FileSystemAutoCompletionDataSource.this.computePrefixLength(text) && text.startsWith(this.text);
		}

		public AutoCompletionResult refine(String text, int cursorPos) {
			text = text.substring(0, cursorPos);
			if(this.canRefine(text)) {
				FileSystemAutoCompletionDataSource.Result result = FileSystemAutoCompletionDataSource.this.new Result(text, this.prefixLength, this.parent);
				result.addFiltedNames(this.results1);
				result.addFiltedNames(this.results2);
				return result;
			} else {
				return null;
			}
		}
	}
}
