package daveayan

import groovy.io.FileType

class GherkinSyntaxChecker {
	public static void main(String[] args) {
		def object = new GherkinSyntaxChecker()
		object.add_dynamic_methods_string()
		object.check_syntax_of_all_files_in "/Users/adave/development/svn-mgh/StudioSpace/atdd/features"
	}
	
	def add_dynamic_methods_string() {
		String.metaClass.is_blank = { ->
			return delegate.trim().length() == 0
		}
		
		String.metaClass.is_not_blank = { ->
			return delegate.trim().length() > 0
		}
		
		String.metaClass.is_a_comment = { ->
			return delegate.is_not_blank() && delegate.trim().startsWith('#')
		}
		
		String.metaClass.is_not_a_comment = { ->
			return delegate.is_not_blank() && ! delegate.trim().startsWith('#')
		}
		
		String.metaClass.is_an_annotation = { ->
			return delegate.is_not_blank() && delegate.trim().startsWith('@')
		}
		
		String.metaClass.is_not_an_annotation = { ->
			return delegate.is_not_blank() && ! delegate.trim().startsWith('@')
		}
	}
	
	def methods_to_handle_keywords = [
		'Feature:' : 'found_feature_on',
		'Scenario:' : 'found_scenario_on',
		'Given' : 'found_given_on',
		'When' : 'found_when_on',
		'Then' : 'found_then_on',
		'And' : 'found_and_on']
	def next_expected_tokens = ['@wip', 'Feature:']
	
	def check_syntax_of_all_files_in(root_folder) {
		def dir = new File(root_folder)
		dir.eachFileRecurse (FileType.FILES) { file ->
			if(file.toString().endsWith('.feature')) {
				check_syntax_of file
			}
		}
	}
	
	def check_syntax_of(file) {
		println file
		next_expected_tokens = ['Feature:']
		def line_number = 1
		file.eachLine { line ->
			if(line.is_not_blank() && line.is_not_a_comment() && line.is_not_an_annotation()) {
				def token_found = line.tokenize()[0]
				if(next_expected_tokens.contains (token_found)) {
					def method_to_call = methods_to_handle_keywords[token_found]
					this."${method_to_call}"(line)
				} else {
					println "At line ${line_number}, Expected one of ${next_expected_tokens}, Found ${token_found} in line ${line}"
				}
			}
			line_number ++
		}
		all_ok()
	}
	
	def found_feature_on(line) {
		next_expected_tokens = ['Scenario:']
	}
	
	def found_scenario_on(line) {
		next_expected_tokens = ['Given', 'When', 'Then', 'Scenario:']
	}
	
	def found_given_on(line) {
		next_expected_tokens = ['And', 'When', 'Then', 'Scenario:']
	}
	
	def found_when_on(line) {
		next_expected_tokens = ['And', 'Then', 'Scenario:']
	}
	
	def found_then_on(line) {
		next_expected_tokens = ['And', 'Scenario:']
	}
	
	def found_and_on(line) {
	}
	
	def all_ok() {
		println "OK\n"
	}
}