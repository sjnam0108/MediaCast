package net.doohad.models;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class CalculateFileSizeVisitor implements FileVisitor<Path> {
	int numFiles = 0;
	int numDirs = 0;
	long sizeSum = 0;
	
	Path onlyThisPath;
	
	@Override
	public FileVisitResult preVisitDirectory(Path dir,
			BasicFileAttributes attrs) throws IOException {
		numDirs ++;

		if (onlyThisPath != null && onlyThisPath != dir) {
			return FileVisitResult.SKIP_SUBTREE;
		}

		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult visitFile(Path file,
			BasicFileAttributes attrs) throws IOException {
		sizeSum += attrs.size();
		numFiles++;
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult visitFileFailed(Path file,
			IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult postVisitDirectory(Path dir,
			IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}
	
	public int getNumFiles() {
		return numFiles;
	}
	
	public int getNumDirs() {
		return numDirs;
	}
	
	public long getSizeSum() {
		return sizeSum;
	}

	public Path getOnlyThisPath() {
		return onlyThisPath;
	}

	public void setOnlyThisPath(Path onlyThisPath) {
		this.onlyThisPath = onlyThisPath;
	}
}
