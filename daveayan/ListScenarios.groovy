package daveayan

import groovy.io.FileType

class ListScenarios {
	public static void main(String[] args) {
		def object = new ListScenarios()
		object.print_scenarios_for_all_feature_files_in("/Users/adave/development/svn-mgh/StudioSpace/atdd/features")
	}
	
	def print_scenarios_for_all_feature_files_in(root_folder) {
		def dir = new File(root_folder)
		dir.eachFileRecurse (FileType.FILES) { file ->
			if(file.toString().endsWith('.feature')) {
				print_scenarios_for_file(file)
			}
		}
	}
	
	def print_scenarios_for_file(file) {
		file.eachLine { line ->
			if(line.trim().startsWith('Feature') || line.trim().startsWith('Scenario'))
				println line
		}
	}
}