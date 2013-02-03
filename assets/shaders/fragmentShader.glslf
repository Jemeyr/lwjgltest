#version 150

out vec4 outColor;

in vec2 texCoordPS;
in vec3 posPS;

smooth in float light;

uniform sampler2D tex;

void main() 
{
	
	outColor = texture(tex, texCoordPS) * light;
	
}

